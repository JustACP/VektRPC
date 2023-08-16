package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.config.ServerConfig;
import top.re1ife.vekt.framework.core.dispatcher.ServerChannelDispatcher;
import top.re1ife.vekt.framework.core.filter.server.ServerFilterChain;
import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;
import top.re1ife.vekt.framework.core.server.ServiceWrapper;
import top.re1ife.vekt.framework.core.spi.ExtensionLoader;

import java.util.HashMap;
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

    /**
     * 过滤器链
     */
    public static ServerFilterChain SERVER_FILTER_CHAIN = new ServerFilterChain();

    /**
     * key：serviceName value：ServiceWrapper
     */
    public static Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new HashMap<>();

    /**
     * SPI
     */
    public static ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();

    public static ServerConfig SERVER_CONFIG;

    public static ServerChannelDispatcher SERVER_CHANNEL_DISPATCHER = new ServerChannelDispatcher();
}
