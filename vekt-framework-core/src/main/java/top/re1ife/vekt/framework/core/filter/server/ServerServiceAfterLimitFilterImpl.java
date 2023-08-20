package top.re1ife.vekt.framework.core.filter.server;

import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.annotations.SPI;
import top.re1ife.vekt.framework.core.common.cache.CommonServerCache;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 16:22:38
 * @Copyright：re1ife | blog: re1ife.top
 */

@SPI("after")
public class ServerServiceAfterLimitFilterImpl implements IServerFilter{
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName)
                .getSemaphore()
                .release();
    }
}
