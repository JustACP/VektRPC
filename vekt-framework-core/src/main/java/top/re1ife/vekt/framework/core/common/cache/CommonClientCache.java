package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.registry.URL;

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

    public static List<String> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<>();

    public static ClientConfig CLIENT_CONFIG;

    public static Map<String, List<URL>> URL_MAP = new ConcurrentHashMap<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    //每次进行远程调用的时候都是从这里面去选择服务提供者
    public static Map<String,List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();


}
