package top.re1ife.vekt.framework.core.filter.client;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.filter.IFilter;

import java.util.List;

public interface IClientFilter extends IFilter {

    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
