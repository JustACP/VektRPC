package top.re1ife.vekt.framework.core.filter.server;

import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.filter.IFilter;

/**
 * 服务端过滤链
 */
public interface IServerFilter extends IFilter {
    void doFilter(RpcInvocation rpcInvocation);
}
