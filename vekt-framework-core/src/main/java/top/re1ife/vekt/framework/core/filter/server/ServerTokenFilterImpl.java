package top.re1ife.vekt.framework.core.filter.server;

import com.alibaba.nacos.common.utils.StringUtils;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.annotations.SPI;
import top.re1ife.vekt.framework.core.server.ServiceWrapper;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;
@SPI("before")
public class ServerTokenFilterImpl implements IServerFilter{


    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String token  = (String) rpcInvocation.getAttachments().get("token");
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        if(serviceWrapper == null){
            return;
        }

        String matchToken = serviceWrapper.getServiceToken();
        if(StringUtils.isBlank(matchToken)){
            return;
        }

        if (StringUtils.isNotBlank(token) && matchToken.equals(token)) {
            return;
        }

        throw new RuntimeException("token is " + token + " verify result is false!");
    }
}
