package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.registery.URL;

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


    /**
     * Provider 名称  该服务有哪些集群URL
     */
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<>();

    public static ClientConfig CLIENT_CONFIG;

    public static Map<String,List<URL>> URL_MAP = new ConcurrentHashMap<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    //调用时从这这里面选择服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

}
