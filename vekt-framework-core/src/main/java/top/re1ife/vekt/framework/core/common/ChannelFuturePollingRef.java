package top.re1ife.vekt.framework.core.common;

import java.util.concurrent.atomic.AtomicLong;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * @author re1ife
 * @description: Providers 轮询
 * @date 2023/08/10 21:43:39
 * @Copyright：re1ife | blog: re1ife.top
 */
public class ChannelFuturePollingRef {

    private AtomicLong referenceTimes = new AtomicLong(0);

    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName){
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTER_MAP.get(serviceName);

        //自增取余数 顺序便利
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % channelFutureWrappers.length);
        return channelFutureWrappers[index];
    }

    public ChannelFutureWrapper getChannelFutureWrapper(ChannelFutureWrapper[] channelFutureWrappers){

        //自增取余数 顺序便利
        long i = referenceTimes.getAndIncrement();
        int index = (int) (i % channelFutureWrappers.length);
        return channelFutureWrappers[index];
    }

}
