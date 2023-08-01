package top.re1ife.vekt.framework.core.client;

import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcDecoder;
import top.re1ife.vekt.framework.core.common.RpcEncoder;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;
import top.re1ife.vekt.framework.core.common.cache.CommonClientCache;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.proxy.jdk.JDKProxyFactory;
import top.re1ife.vekt.framework.interfaces.DataService;

import java.net.Socket;

/**
 * @author re1ife
 * @description:
 * @date 2023/07/31 21:28:10
 * @Copyright：re1ife | blog: re1ife.top
 */
public class Client {

    private Logger logger = LoggerFactory.getLogger(Client.class);

    public static EventLoopGroup clientGroup = new NioEventLoopGroup();

    @Getter
    @Setter
    private ClientConfig clientConfig;

    /**
     * 客户端需要通过一个代理工厂获取被调用对象的代理对象，然后通过代理对象将数据放入发送队列
     *  最后有一个异步线程将发送队列内部的数据一个个地发送到服务端，并且等待服务端响应对应的数据结果
     */
    public RpcReference startClientApplication(){
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //添加流水线那
                socketChannel.pipeline().addLast(new RpcEncoder())
                        .addLast(new RpcDecoder())
                        .addLast(new ClientHandler());

            }
        });

        //常规连接Netty服务端
        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getServerAddr(),clientConfig.getPort());
        logger.info("client start!");
        this.startClient(channelFuture);
        RpcReference reference = new RpcReference(new JDKProxyFactory());
        return reference;
    }

    /**
     * 异步发送消息
     */
    class AsyncSendJob implements Runnable{
        private ChannelFuture channelFuture;

        public AsyncSendJob(ChannelFuture channelFuture){
            this.channelFuture = channelFuture;
        }


        @Override
        public void run() {
            logger.info("Send Thread Start");
            while(true){
                try{
                    //阻塞模式
                    RpcInvocation data = CommonClientCache.SEND_QUEUE.take();
                    logger.info("data : {}",JSONObject.toJSONString(data));
                    String json = JSONObject.toJSONString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());
                    //发送数据
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                    logger.info("Message Send Success!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 创建发送线程，将数据包发给服务端
     */
    private void startClient(ChannelFuture channelFuture){
        //请求发送任务交给单独IO线程负责、实现异步
        Thread asyncSendJob = new Thread(new AsyncSendJob(channelFuture));
        asyncSendJob.start();
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setPort(8999);
        clientConfig.setServerAddr("localhost");
        client.setClientConfig(clientConfig);

        RpcReference rpcReference = client.startClientApplication();
        DataService dataService = rpcReference.get(DataService.class);
        for(int i = 0; i < 100;i++){
            String result = dataService.sendData("test");
            System.out.println(Thread.currentThread() + ": " + result);
        }
    }
}

