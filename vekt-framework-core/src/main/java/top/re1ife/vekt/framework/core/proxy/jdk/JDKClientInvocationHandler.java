package top.re1ife.vekt.framework.core.proxy.jdk;

import top.re1ife.vekt.framework.core.client.RpcReferenceWrapper;
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

    private RpcReferenceWrapper rpcReferenceWrapper;

    private Long timeout;

    public JDKClientInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
        this.timeout = Long.valueOf(rpcReferenceWrapper.getTimeOUt());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());

        //注入UUID 每次请求单独区分
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        SEND_QUEUE.add(rpcInvocation);

        if(rpcReferenceWrapper.isAsync()){
            return null;
        }
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);

        long beginTime = System.currentTimeMillis();

        //客户端请求超时依据
        while(System.currentTimeMillis() - beginTime < timeout){
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if(object instanceof RpcInvocation){
                return ((RpcInvocation) object).getResponse();
            }
        }

        throw new TimeoutException("Wait for response from server on client " +rpcReferenceWrapper.getTimeOUt() + "ms,Server's name is " +rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
    }
}
