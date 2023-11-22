package top.re1ife.vekt.framework.core.common.event.listener;

import top.re1ife.vekt.framework.core.common.event.VektRpcDestoryEvent;
import top.re1ife.vekt.framework.core.registry.URL;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.REGISTRY_SERVICE;

/**
 * 服务洗哦啊会事件监听
 */
public class ServiceDestoryListener implements VektRpcListener<VektRpcDestoryEvent>{
    @Override
    public void callBack(Object t) {
        for (URL url : PROVIDER_URL_SET) {
            REGISTRY_SERVICE.unRegister(url);
        }

    }
}
