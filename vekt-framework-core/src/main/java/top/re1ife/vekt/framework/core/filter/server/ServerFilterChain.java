package top.re1ife.vekt.framework.core.filter.server;

import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端过滤链
 */
public class ServerFilterChain {
    private static List<IServerFilter> iServerFilters = new ArrayList<>();

    public void addServerFilter(IServerFilter serverFilter){
        iServerFilters.add(serverFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation){
        for(IServerFilter iServerFilter : iServerFilters){
            iServerFilter.doFilter(rpcInvocation);
        }
    }
}
