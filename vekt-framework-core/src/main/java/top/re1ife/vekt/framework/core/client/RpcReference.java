package top.re1ife.vekt.framework.core.client;

import top.re1ife.vekt.framework.core.proxy.ProxyFactory;

/**
 * @author re1ife
 * @date 2023/07/31 21:32:44
 * @Copyright：re1ife | blog: re1ife.top
 */
public class RpcReference {
    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory){
        this.proxyFactory = proxyFactory;
    }

    /**
     * 根据接口类型获取代理对象
     */
    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
