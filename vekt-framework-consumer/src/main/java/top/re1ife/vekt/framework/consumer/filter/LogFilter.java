package top.re1ife.vekt.framework.consumer.filter;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.filter.client.IClientFilter;

import java.util.List;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/19 20:19:10
 * @Copyright：re1ife | blog: re1ife.top
 */
public class LogFilter implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        System.out.println("自定义日志过滤器");
    }
}
