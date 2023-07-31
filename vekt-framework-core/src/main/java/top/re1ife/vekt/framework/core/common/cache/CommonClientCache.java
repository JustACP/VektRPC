package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共缓存，存储请求队列等公共信息
 */
public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    public static Map<String, Object> RESP_MAP = new ConcurrentHashMap<>();

}
