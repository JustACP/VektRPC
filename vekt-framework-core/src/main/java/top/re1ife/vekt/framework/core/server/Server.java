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
import top.re1ife.vekt.framework.core.config.ServerConfig;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup wokrerGroup = null;

    private Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConfig serverConfig;

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

        bootstrap.bind(serverConfig.getPort()).sync();
    }

    public void registryService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }

        Class<?>[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1){
            throw new RuntimeException("service must only had one interfaces!");
        }
        Class<?> interfaceClass = classes[0];
        //需要注册的对象统一放到一个Map集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(),serviceBean);

    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(8999);
        server.setServerConfig(serverConfig);
        server.registryService(new DataServiceImpl());
        server.startApplication();;
    }



}
