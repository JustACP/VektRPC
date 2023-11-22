package top.re1ife.vekt.framework.core.proxy.jdk;

import top.re1ife.vekt.framework.core.client.RpcReferenceWrapper;
import top.re1ife.vekt.framework.core.common.config.PropertiesBootstrap;
import top.re1ife.vekt.framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;


/**
 * @author re1ife
 * @description:
 * @date 2023/07/31 20:35:15
 * @Copyright：re1ife | blog: re1ife.top
 */
public class JDKProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) {
        return (T) Proxy.newProxyInstance(rpcReferenceWrapper.getAimClass().getClassLoader(),
                new Class[]{rpcReferenceWrapper.getAimClass()}, new JDKClientInvocationHandler(rpcReferenceWrapper));
    }
}
