package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.URL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class CommonServerCache {

    public static Map<String,Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();

    public static Set<URL> PROVIDER_URL_SET = new CopyOnWriteArraySet<top.re1ife.vekt.framework.core.registry.URL>();

    public static RegistryService REGISTRY_SERVICE;
}
