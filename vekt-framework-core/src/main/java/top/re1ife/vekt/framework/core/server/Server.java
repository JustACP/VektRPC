package top.re1ife.vekt.framework.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcDecoder;
import top.re1ife.vekt.framework.core.common.RpcEncoder;
import top.re1ife.vekt.framework.core.common.config.PropertiesBootstrap;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;
import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;
import top.re1ife.vekt.framework.core.common.utils.CommonUtils;
import top.re1ife.vekt.framework.core.config.ServerConfig;
import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.nacos.NacosRegister;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.*;

public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup wokrerGroup = null;

    private Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConfig serverConfig;

    private static VektRpcListenerLoader vektRpcListenerLoader;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

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

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                logger.info("Server Init Provider");
                socketChannel.pipeline().addLast(new RpcEncoder())
                        .addLast(new RpcDecoder())
                        .addLast(new ServerHandler());
            }
        });

        this.batchExportUrl();

        bootstrap.bind(serverConfig.getPort()).sync();
    }


    /**
     * 暴露服务信息
     * @param serviceBean
     */
    public void exportService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }

        Class<?>[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1){
            throw new RuntimeException("service must only had one interfaces!");
        }

        if(REGISTRY_SERVICE == null){
            REGISTRY_SERVICE = new NacosRegister(serverConfig.getRegisterAddr());
        }

        Class<?> interfaceClass = classes[0];
        //需要注册的对象统一放到一个Map集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(),serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setGroupName(NacosConstant.DEFAULT_GROUP_NAME);
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(serverConfig.getPort()));
        PROVIDER_URL_SET.add(url);
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

    public void initServerConfig(){
        ServerConfig serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.initServerConfig();
        vektRpcListenerLoader = new VektRpcListenerLoader();
        vektRpcListenerLoader.init();
        server.exportService(new DataServiceImpl());
         server.startApplication();
        //注册destroy钩子函数
        ApplicationShutdownHook.registryShutdownHook();
    }



}
