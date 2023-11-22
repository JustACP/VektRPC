package top.re1ife.vekt.framework.core.router;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.registry.URL;

/**
 * @author re1ife
 * @description: 路由
 * @date 2023/08/10 22:46:51
 * @Copyright：re1ife | blog: re1ife.top
 */
public interface VektRouter {

    /**
     * 刷新路由数组
     * @param selector
     */
    void refreshRouterArr(Selector selector);

    /**
     * 对应 Provider 连接通道
     * @param selector
     * @return
     */
    ChannelFutureWrapper select(Selector selector);

    void updateWeight(URL url);
}
