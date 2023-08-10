package top.re1ife.vekt.framework.core.registry.nacos;

import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;

import java.util.List;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

public abstract class AbstractRegister implements RegistryService {
    @Override
    public void Register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url);
    }

    @Override
    public void doUnSubscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url);
    }

    /**
     * 留给子类扩展
     * @param url
     */
    public abstract void doAfterSubscribe(URL url);

    /**
     * 留给子类扩展
     */
    public abstract List<String> getProviderIps(String serviceName);
}
