package top.re1ife.vekt.framework.core.common.event.listener;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.client.ConnectionHandler;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.cache.CommonClientCache;
import top.re1ife.vekt.framework.core.common.event.VektUpdateEvent;
import top.re1ife.vekt.framework.core.common.event.data.URLChangeWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceUpdateListener implements VektRpcListener<VektUpdateEvent>{

    private static final Logger logger = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callBack(Object t) {
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CommonClientCache.CONNECT_MAP.get(urlChangeWrapper.getServiceName());
        if(channelFutureWrappers.isEmpty()){
            logger.error("[ServiceUpdateListener] channelFutureWrapper is empty");
            return;
        }

        //最新的 provider
        List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();
        Set<String> finalUrl = new HashSet<>();
        List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();

        // 移除旧URL
        for(ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers){
            StringBuilder st = new StringBuilder(channelFutureWrapper.getHost()).append(":").append(channelFutureWrapper.getPort());
            String oldServiceAddress =  st.toString();

            if(matchProviderUrl.contains(oldServiceAddress)){
                finalChannelFutureWrappers.add(channelFutureWrapper);
                finalUrl.add(oldServiceAddress);
            }
        }

        //增加新的provider
        //此时老的已经被移除，开始检查是否有新的url
        //ChannelFutureWrapper是一个自定义的包装类，将netty建立好的ChannelFuture做了一些封装

        List<ChannelFutureWrapper> newChannelFutureWrapper = new ArrayList<>();
        for(String newProviderUrl : matchProviderUrl){
            if(!finalUrl.contains(newProviderUrl)){
                //新的 URL
                ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
                String host = newProviderUrl.split(":")[0];
                Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);
                channelFutureWrapper.setPort(port);
                channelFutureWrapper.setHost(host);
                ChannelFuture channelFuture = null;

                try {
                    channelFuture = ConnectionHandler.createChannelFuture(host, port);
                    channelFutureWrapper.setChannelFuture(channelFuture);
                    newChannelFutureWrapper.add(channelFutureWrapper);
                    finalUrl.add(newProviderUrl);

                } catch (InterruptedException e) {
                    logger.error("ServiceUpdateListener callback error",e);
                }
            }

            finalChannelFutureWrappers.addAll(newChannelFutureWrapper);
            //最终更新服务
            CommonClientCache.CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
        }

    }
}
