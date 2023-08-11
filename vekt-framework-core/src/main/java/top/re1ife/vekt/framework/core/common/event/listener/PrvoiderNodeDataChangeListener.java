package top.re1ife.vekt.framework.core.common.event.listener;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.event.VektRpcNodeChangeEvent;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.nacos.ProviderNodeInfo;

import java.util.List;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.CONNECT_MAP;
import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.VEKT_ROUTER;

public class PrvoiderNodeDataChangeListener implements VektRpcListener<VektRpcNodeChangeEvent> {
    @Override
    public void callBack(Object t) {
        ProviderNodeInfo providerNodeInfo = (ProviderNodeInfo) t;
        List<ChannelFutureWrapper>  channelFutureWrappers = CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String address = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if(address.equals(providerNodeInfo.getAddress())){
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                VEKT_ROUTER.updateWeight(url);
            }
        }

    }
}
