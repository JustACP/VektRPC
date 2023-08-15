package top.re1ife.vekt.framework.core.proxy;

import top.re1ife.vekt.framework.core.client.RpcReferenceWrapper;

/**
 * @author re1ife
 * @description: 代理工厂
 * @date 2023/07/31 20:33:46
 * @Copyright：re1ife | blog: re1ife.top
 */
public interface ProxyFactory {
    <T> T getProxy(final RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable;

}
