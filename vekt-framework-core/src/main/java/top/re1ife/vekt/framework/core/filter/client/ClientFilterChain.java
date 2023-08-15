package top.re1ife.vekt.framework.core.filter.client;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端过滤器
 */
public class ClientFilterChain {

    private static List<IClientFilter> iClientFilters = new ArrayList<>();

    public void addServerFilter(IClientFilter iClientFilter){
        iClientFilters.add(iClientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation){
        for(IClientFilter iClientFilter : iClientFilters){
            iClientFilter.doFilter(src, rpcInvocation);
        }
    }
}
