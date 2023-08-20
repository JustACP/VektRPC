package top.re1ife.vekt.framework.core.client;

import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcDecoder;
import top.re1ife.vekt.framework.core.common.RpcEncoder;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;
import top.re1ife.vekt.framework.core.common.cache.CommonClientCache;
import top.re1ife.vekt.framework.core.common.config.PropertiesBootstrap;
import top.re1ife.vekt.framework.core.common.constant.RpcConstants;
import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;
import top.re1ife.vekt.framework.core.common.utils.CommonUtils;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.filter.client.ClientFilterChain;
import top.re1ife.vekt.framework.core.filter.client.IClientFilter;
import top.re1ife.vekt.framework.core.proxy.jdk.JDKProxyFactory;
import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.nacos.AbstractRegister;
import top.re1ife.vekt.framework.core.registry.nacos.NacosRegister;
import top.re1ife.vekt.framework.core.router.VektRouter;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.*;
import static top.re1ife.vekt.framework.core.common.constant.RpcConstants.DEFAULT_DECODE_CHAR;
import static top.re1ife.vekt.framework.core.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

/**
 * @author re1ife
 * @description:
 * @date 2023/07/31 21:28:10
 * @Copyright：re1ife | blog: re1ife.top
 */
public class Client {

    private Logger logger = LoggerFactory.getLogger(Client.class);


    public static EventLoopGroup clientGroup = new NioEventLoopGroup();



    private AbstractRegister abstractRegister;

    private VektRpcListenerLoader vektRpcListenerLoader;

    @Getter
    private Bootstrap bootstrap = new Bootstrap();


    public RpcReference initClientApplication() throws Exception {
        return initClientApplication(PropertiesBootstrap.loadClientConfigFromLocal());
    }
    /**
     * 客户端需要通过一个代理工厂获取被调用对象的代理对象，然后通过代理对象将数据放入发送队列
     *  最后有一个异步线程将发送队列内部的数据一个个地发送到服务端，并且等待服务端响应对应的数据结果
     */
    public RpcReference initClientApplication(ClientConfig clientConfig) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer(DEFAULT_DECODE_CHAR.getBytes());
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(CLIENT_CONFIG.getMaxServerRespDataSize(), delimiter));

                //添加流水线那
                socketChannel.pipeline()
                        .addLast(new RpcEncoder())
                        .addLast(new RpcDecoder())
                        .addLast(new ClientHandler());

            }
        });

        vektRpcListenerLoader = new VektRpcListenerLoader();
        vektRpcListenerLoader.init();

        CLIENT_CONFIG = clientConfig;
        this.initConfig();
        RpcReference rpcReference = null;
        if("javassist".equals(CLIENT_CONFIG.getProxyType())){
//            rpcReference = new RpcReference(new )
        } else {
            rpcReference = new RpcReference(new JDKProxyFactory());
        }
        return  rpcReference;

    }

    public void doSubscribeService(Class serviceBean){
        if(abstractRegister == null){
            try{
                EXTENSION_LOADER.loadExtension(RegistryService.class);
                LinkedHashMap<String, Class> registerClassMap = EXTENSION_LOADER_CLASS_CACHE.get(RegistryService.class.getName());
                Class registerClass = registerClassMap.get(CLIENT_CONFIG.getRegisterType());
                abstractRegister = (AbstractRegister) registerClass.newInstance();
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        URL url = new URL();
        url.setApplicationName(CLIENT_CONFIG.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());
        Map<String, String> result = abstractRegister.getServiceWeightMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);
        abstractRegister.subscribe(url);
    }

    /**
     * 开始和各个provider建立链接
     */
    public void doConnectServer(){
        for (URL url : CommonClientCache.SUBSCRIBE_SERVICE_LIST) {
            List<String> providers = abstractRegister.getProviderIps(url.getServiceName());
            for (String provider : providers) {
                try {
                    ConnectionHandler.connect(url.getServiceName(), provider);
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] connect fail", e);
                }
            }
            url.addParameter("providerIps", JSONObject.toJSONString(providers));
            abstractRegister.doAfterSubscribe(url);
        }
    }

    /**
     * 异步发送消息
     */
    class AsyncSendJob implements Runnable{


        public AsyncSendJob(){

        }


        @Override
        public void run() {
            logger.info("Send Thread Start");
            RpcInvocation rpcInvocation = null;
            while(true){
                try {
                    //阻塞模式
                    rpcInvocation = CommonClientCache.SEND_QUEUE.take();
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(rpcInvocation);
                    //判断channel是否已经中断
                    if (channelFuture.channel().isOpen()) {
                        //将RpcInvocation封装到RpcProtocol对象中，然后发送给服务端，这里正好对应了ServerHandler
                        RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(rpcInvocation));
                        channelFuture.channel().writeAndFlush(rpcProtocol);
                    }
                } catch (Exception e) {
                    logger.error("client call error", e);
                    rpcInvocation.setE(e);
                    RESP_MAP.put(rpcInvocation.getUuid(), rpcInvocation);
                }
            }
        }
    }

    /**
     * 创建发送线程，将数据包发给服务端
     */
    public void startClient(){
        //请求发送任务交给单独IO线程负责、实现异步
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }



    private void initConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //初始化路由策略
        EXTENSION_LOADER.loadExtension(VektRouter.class);
        String routeStrategy = CLIENT_CONFIG.getRouterStrategy();
        LinkedHashMap<String, Class> vektRouterMap = EXTENSION_LOADER_CLASS_CACHE.get(VektRouter.class.getName());
        Class vektRouterClass = vektRouterMap.get(routeStrategy);
        if(vektRouterClass == null) {
            throw new RuntimeException("no match routerStrategy for " + routeStrategy);
        }
        VEKT_ROUTER = (VektRouter) vektRouterClass.newInstance();

        //初次序列化方式
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String serializeType = CLIENT_CONFIG.getClientSerializeType();
        LinkedHashMap<String, Class> serializeTypeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeClass = serializeTypeMap.get(serializeType);
        if(serializeClass == null){
            throw new RuntimeException("no match serialize type for " + serializeType);
        }
        CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();

        //初始化过滤链
        EXTENSION_LOADER.loadExtension(IClientFilter.class);
        ClientFilterChain clientFilterChain = new ClientFilterChain();
        LinkedHashMap<String, Class> filterMap = EXTENSION_LOADER_CLASS_CACHE.get(IClientFilter.class.getName());
        for (String implClassName : filterMap.keySet()) {
            Class filterClass = filterMap.get(implClassName);
            if(filterClass == null){
                throw new NullPointerException("no match client filter for " + implClassName);
            }
            clientFilterChain.addServerFilter((IClientFilter) filterClass.newInstance());
        }
        CLIENT_FILTER_CHAIN = clientFilterChain;

    }
}

