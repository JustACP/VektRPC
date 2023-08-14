package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class CommonServerCache {

    public static Map<String,Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();

    public static Set<URL> PROVIDER_URL_SET = new CopyOnWriteArraySet<top.re1ife.vekt.framework.core.registry.URL>();

    public static RegistryService REGISTRY_SERVICE;

    /**
     * 服务端端序列化方式
     */
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;
}
