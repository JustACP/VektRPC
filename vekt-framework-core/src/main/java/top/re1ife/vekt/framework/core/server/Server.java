package top.re1ife.vekt.framework.core.server;

import com.alibaba.nacos.common.utils.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcDecoder;
import top.re1ife.vekt.framework.core.common.RpcEncoder;
import top.re1ife.vekt.framework.core.common.ServerServiceSemaphoreWrapper;
import top.re1ife.vekt.framework.core.common.annotations.SPI;
import top.re1ife.vekt.framework.core.common.config.PropertiesBootstrap;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;
import top.re1ife.vekt.framework.core.common.constant.RpcConstants;
import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;
import top.re1ife.vekt.framework.core.common.utils.CommonUtils;
import top.re1ife.vekt.framework.core.config.ServerConfig;
import top.re1ife.vekt.framework.core.filter.server.IServerFilter;
import top.re1ife.vekt.framework.core.filter.server.ServerFilterChain;
import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.nacos.AbstractRegister;
import top.re1ife.vekt.framework.core.registry.nacos.NacosRegister;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;
import top.re1ife.vekt.framework.core.serialize.fastjson.FastJsonSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.hessian.HessianSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.jdk.JdkSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.kroy.KroySerializeFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.*;
import static top.re1ife.vekt.framework.core.common.constant.RpcConstants.*;
import static top.re1ife.vekt.framework.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup wokrerGroup = null;

    private ServerHandler serverHandler;

    private MaxConnectionLimitHandler maxConnectionLimitHandler;

    private Logger logger = LoggerFactory.getLogger(Server.class);



    private static VektRpcListenerLoader vektRpcListenerLoader;



    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        wokrerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, wokrerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        //设置TCP的Nagle算法为禁用，即启用TCP的无延迟发送
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //设置服务器的TCP连接队列大小
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        //发送缓冲区大小为16KB，表示可以缓存的待发送数据的最大字节数
        //接收缓冲区大小为16KB，表示可以缓存的待接收数据的最大字节数
        //设置TCP的Keep-Alive机制为启用
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);

        maxConnectionLimitHandler = new MaxConnectionLimitHandler(SERVER_CONFIG.getMaxConnections());
        bootstrap.handler(maxConnectionLimitHandler);

        serverHandler = new ServerHandler();
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                logger.info("初始化provider过程");
                ByteBuf delimiter = Unpooled.copiedBuffer(RpcConstants.DEFAULT_DECODE_CHAR.getBytes());
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(SERVER_CONFIG.getMaxServerRequestData(), delimiter));
                socketChannel.pipeline().addLast(new RpcEncoder())
                        .addLast(new RpcDecoder())
                        .addLast(serverHandler);
            }
        });

        this.batchExportUrl();
        SERVER_CHANNEL_DISPATCHER.startDataConsume();
        bootstrap.bind(SERVER_CONFIG.getPort()).sync();
    }


    /**
     * 暴露服务信息
     * @param serviceWrapper
     */
    public void exportService(ServiceWrapper serviceWrapper) {
        Object serviceBean = serviceWrapper.getServerObj();
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }

        Class<?>[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1){
            throw new RuntimeException("service must only had one interfaces!");
        }

        if(REGISTRY_SERVICE == null){
            try{
                //使用自定义的SPI机制加载配置
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                LinkedHashMap<String, Class> registerClassMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registerClass = registerClassMap.get(SERVER_CONFIG.getRegisterType());
                //实例化SPI对象
                REGISTRY_SERVICE = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("registryServiceType unKnow, error is ", e);
            }
        }

        Class<?> interfaceClass = classes[0];
        //需要注册的对象统一放到一个Map集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(SERVER_CONFIG.getApplicationName());
        url.setGroupName(NacosConstant.DEFAULT_GROUP_NAME);
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(SERVER_CONFIG.getPort()));
        url.addParameter("group", serviceWrapper.getGroupName());
        url.addParameter("limit", String.valueOf(serviceWrapper.getLimit()));
        url.addParameter("application", SERVER_CONFIG.getApplicationName());

        //设置服务端的限流器
        SERVER_SERVICE_SEMAPHORE_MAP.put(interfaceClass.getName(),new ServerServiceSemaphoreWrapper(serviceWrapper.getLimit()));
        PROVIDER_URL_SET.add(url);
        if (StringUtils.isNotBlank(serviceWrapper.getServiceToken())){
            PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(),serviceWrapper);
        }
    }

    /**
     * 为了将服务端的具体访问都暴露到注册中心
     */
    private  void batchExportUrl(){
        Thread task = new Thread(() -> {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (URL url : PROVIDER_URL_SET) {
                REGISTRY_SERVICE.Register(url);
            }
        });

        task.start();
    }

    public void initServerConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        SERVER_CONFIG = PropertiesBootstrap.loadServerConfigFromLocal();
        //初始化线程池和队列配置
        SERVER_CHANNEL_DISPATCHER.init(SERVER_CONFIG.getServerQueueSize(), SERVER_CONFIG.getServerBizThreadNums());
        //初始化序列化方式
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String serializeType = SERVER_CONFIG.getServerSerializeType();
        LinkedHashMap<String, Class> serializeTypeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeClass = serializeTypeMap.get(serializeType);
        if (serializeClass == null) {
            throw new RuntimeException("no match serialize type for " + serializeType);
        }
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();

        //初始化过滤链
        EXTENSION_LOADER.loadExtension(IServerFilter.class);
        ServerFilterChain serverBeforeFilterChain = new ServerFilterChain();
        ServerFilterChain serverAfterFilterChain = new ServerFilterChain();
        LinkedHashMap<String, Class> filterMap = EXTENSION_LOADER_CLASS_CACHE.get(IServerFilter.class.getName());
        for (String implClassName : filterMap.keySet()) {
            Class filterClass = filterMap.get(implClassName);
            if (filterClass == null) {
                throw new NullPointerException("no match server filter for " + implClassName);
            }
            Annotation spiAnnotation = filterClass.getDeclaredAnnotation(SPI.class);
            if (spiAnnotation == null) {
                logger.warn("filter {}spi annotation is null ", filterClass.getName());
                continue;
            }
            SPI spi = (SPI) spiAnnotation;
            if("before".equals(spi.value())){
                serverBeforeFilterChain.addServerFilter((IServerFilter) filterClass.newInstance());
            }else if("after".equals(spi.value())){
                serverAfterFilterChain.addServerFilter((IServerFilter) filterClass.newInstance());
            }

        }
        SERVER_BEFORE_FILTER_CHAIN = serverBeforeFilterChain;
        SERVER_AFTER_FILTER_CHAIN = serverAfterFilterChain;
    }

//    public static void main(String[] args) throws InterruptedException {
//        Server server = new Server();
//        server.initServerConfig();
//        vektRpcListenerLoader = new VektRpcListenerLoader();
//        vektRpcListenerLoader.init();
//        server.exportService(new ServiceWrapper(new DataServiceImpl()));
//        server.exportService(new ServiceWrapper(new UserServiceImpl()));
//         server.startApplication();
//        //注册destroy钩子函数
//        ApplicationShutdownHook.registryShutdownHook();
//    }



}
