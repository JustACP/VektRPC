package top.re1ife.vekt.framework.core.proxy.jdk;

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
    public <T> T getProxy(Class clazz) throws Throwable {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new JDKClientInvocationHandler(clazz));
    }
}
