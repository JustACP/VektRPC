package top.re1ife.vekt.framework.core.client;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.nacos.ProviderNodeInfo;
import top.re1ife.vekt.framework.core.router.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.*;

/**
 * Description：将连接的建立、断开、按照服务名筛选等功能都封装在了一起，
 *  按照单一指责的设计原则，将与连接有关的功能都统一封装在了一起
 */
public class ConnectionHandler {
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap){
        ConnectionHandler.bootstrap = bootstrap;
    }

    /**
     * 构建单个连接通道，元操作
     * @param serviceName
     * @param ip
     * @param port
     * @throws InterruptedException
     */
    public static void connect(String serviceName, String ip, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setHost(ip);
        channelFutureWrapper.setPort(port);
        StringBuilder st = new StringBuilder();
        st.append(ip).append(":").append(port);


        //获取权重
        ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(URL_MAP.get(serviceName).get(st.toString()));
        Double weight = providerNodeInfo.getWeight();
        channelFutureWrapper.setWeight(weight);
        channelFutureWrapper.setGroup(providerNodeInfo.getGroup());
        SERVER_ADDRESS.add(st.toString());

        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if(CollectionUtils.isEmpty(channelFutureWrappers)){
            channelFutureWrappers = new ArrayList<>();
        }

        channelFutureWrappers.add(channelFutureWrapper);
        CONNECT_MAP.put(serviceName, channelFutureWrappers);

        Selector selector = new Selector();
        selector.setProviderServiceName(serviceName);
        VEKT_ROUTER.refreshRouterArr(selector);
    }

    public static void connect(String serviceName, String providerIpAndPort) throws InterruptedException{
        if(bootstrap == null){
            throw new RuntimeException("bootstrap can not be null");
        }

        //格式错误
        if(!providerIpAndPort.contains(":")){
            return;
        }

        String[] providerAddress = providerIpAndPort.split(":");
        String ip = providerAddress[0];
        int port = Integer.parseInt(providerAddress[1]);
        connect(serviceName, ip, port);
    }

    public static ChannelFuture createChannelFuture(String host, Integer port) throws InterruptedException {
        return bootstrap.connect(host,port).sync();
    }

    /**
     * 断开连接
     * @param providerServiceName 服务名
     * @param providerIp 服务提供者IP
     */
    public static void disConnect(String providerServiceName,String providerIp){
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (!channelFutureWrappers.isEmpty()){
            channelFutureWrappers.removeIf(channelFutureWrapper ->
                    providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()));
        }
    }

    /**
     * 默认走随机策略获取ChannelFuture
     */
    public static ChannelFuture getChannelFuture(String providerServiceName){


        Selector selector = new Selector();
        selector.setProviderServiceName(providerServiceName);
        ChannelFutureWrapper channelFutureWrapper = VEKT_ROUTER.select(selector);
        if(channelFutureWrapper == null){
            String message = String.format("no service %s provider", providerServiceName);
            throw new RuntimeException(message);
        }

        return channelFutureWrapper.getChannelFuture();
    }

    public static ChannelFuture getChannelFuture(RpcInvocation rpcInvocation){
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTER_MAP.get(rpcInvocation.getTargetServiceName());
        if(channelFutureWrappers == null || channelFutureWrappers.length == 0){
            throw new RuntimeException("no provider exist for " + rpcInvocation.getTargetServiceName());
        }

        ArrayList<ChannelFutureWrapper> wrapperArrayList = Lists.newArrayList(channelFutureWrappers);

        //doFilter
        CLIENT_FILTER_CHAIN.doFilter(wrapperArrayList, rpcInvocation);
        Selector selector = new Selector();
        selector.setProviderServiceName(rpcInvocation.getTargetServiceName());
        ChannelFutureWrapper[] channelFutureWrappersCopy = new ChannelFutureWrapper[wrapperArrayList.size()];
        selector.setChannelFutureWrappers(wrapperArrayList.toArray(channelFutureWrappersCopy));
        return VEKT_ROUTER.select(selector).getChannelFuture();
    }


}
