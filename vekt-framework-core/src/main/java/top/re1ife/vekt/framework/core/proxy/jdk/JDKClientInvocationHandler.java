package top.re1ife.vekt.framework.core.proxy.jdk;

import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.RESP_MAP;
import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.SEND_QUEUE;

/**
 * @author re1ife
 * @description:
 * @date 2023/07/31 20:39:22
 * @Copyright：re1ife | blog: re1ife.top
 */
public class JDKClientInvocationHandler implements InvocationHandler {

    private final static Object OBJECT = new Object();

    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(clazz.getName());

        //注入UUID 每次请求单独区分
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        SEND_QUEUE.add(rpcInvocation);
        long beginTime = System.currentTimeMillis();

        //客户端请求超时依据
        while(System.currentTimeMillis() - beginTime < 3 * 1000){
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if(object instanceof RpcInvocation){
                return ((RpcInvocation) object).getResponse();
            }
        }

        throw new TimeoutException("client wait server's response timeout!");
    }
}
