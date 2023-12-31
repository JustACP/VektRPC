package top.re1ife.vekt.framework.core.common.cache;

import com.alibaba.nacos.api.naming.listener.EventListener;
import top.re1ife.vekt.framework.core.common.ChannelFuturePollingRef;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.filter.client.ClientFilterChain;
import top.re1ife.vekt.framework.core.registry.URL;
import top.re1ife.vekt.framework.core.router.VektRouter;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;
import top.re1ife.vekt.framework.core.spi.ExtensionLoader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 公共缓存，存储请求队列等公共信息
 */
public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    public static Map<String, Object> RESP_MAP = new ConcurrentHashMap<>();

    public static List<URL> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<URL>();

    public static Map<String, EventListener> SERVICE_LISTENER = new ConcurrentHashMap<>();


    public static Map<String, Map<String, String>> URL_MAP = new ConcurrentHashMap<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String,List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    //随机请求的Map，key：serviceName，value：provider channel列表
    public static Map<String, ChannelFutureWrapper[]>  SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();

    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();

    public static VektRouter VEKT_ROUTER;

    /**
     * 客户端序列化方式
     */
    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;

    /**
     * 客户端过滤链
     */
    public static ClientFilterChain CLIENT_FILTER_CHAIN;


    public static ClientConfig CLIENT_CONFIG;

    public static ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();



}
