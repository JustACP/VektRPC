package top.re1ife.vekt.framework.core.router;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.registry.URL;
import java.util.List;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.*;

/**
 * @author re1ife
 * @description: 轮询访问路由
 * @date 2023/08/10 23:09:15
 * @Copyright：re1ife | blog: re1ife.top
 */
public class RotateRouterImpl implements VektRouter{
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];

        //提权生成调用先后顺序的数组

        for(int i = 0; i < arr.length; i++){
            arr[i] = channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrap(selector.getProviderServiceName());
    }

    @Override
    public void updateWeight(URL url) {

    }
}
