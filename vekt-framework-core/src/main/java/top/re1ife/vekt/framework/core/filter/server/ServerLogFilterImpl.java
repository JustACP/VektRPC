package top.re1ife.vekt.framework.core.filter.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.annotations.SPI;

/**
 * 服务端日志过滤器
 */
@SPI("before")
public class ServerLogFilterImpl implements IServerFilter{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerLogFilterImpl.class);
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        LOGGER.info(rpcInvocation.getAttachments().get("c_app_name") + "do invoke ------>" + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
