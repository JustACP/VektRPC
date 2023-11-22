package top.re1ife.vekt.framework.core.filter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.List;
import java.util.Map;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.CLIENT_CONFIG;

public class ClientLogFilterImpl implements IClientFilter{
    private static final Logger logger = LoggerFactory.getLogger(ClientLogFilterImpl.class);
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        Map<String, Object> attachments = rpcInvocation.getAttachments();
        attachments.put("c_app_name", CLIENT_CONFIG.getApplicationName());
        logger.info(rpcInvocation.getAttachments().get("c_app_name")+" do invoke -----> "+rpcInvocation.getTargetServiceName());
    }
}
