package top.re1ife.vekt.framework.consumer.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.router.Selector;
import top.re1ife.vekt.framework.core.router.VektRouter;

import java.util.List;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.*;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/19 20:11:36
 * @Copyright：re1ife | blog: re1ife.top
 */
public class RotateRouterImplV2 implements VektRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RotateRouterImplV2.class);

    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        for(int i = 0; i < channelFutureWrappers.size(); i++){
            arr[i] = channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {

        LOGGER.info("RotateRouterImplV2执行.......");
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getChannelFutureWrappers());
    }

    @Override
    public void updateWeight(URL url) {

    }
}
