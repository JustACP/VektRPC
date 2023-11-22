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
        //这里面注入了一个uuid，对每一次的请求都单独区分
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        rpcInvocation.setRetry(rpcReferenceWrapper.getRetry());
        try {
            SEND_QUEUE.add(rpcInvocation);

            if (rpcReferenceWrapper.isAsync()) {
                return null;
            }
            RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);

            long beginTime = System.currentTimeMillis();
            long nowTimeMillis = System.currentTimeMillis();

            //总重试次数
            int retryTimes = 0;
            //客户端请求超时的判断依据
            while (true) {
                Object object = RESP_MAP.get(rpcInvocation.getUuid());
                if (object instanceof RpcInvocation) {
                    RpcInvocation rpcInvocationResp = (RpcInvocation) object;
                    if (rpcInvocationResp.getE() == null) {
                        return rpcInvocationResp.getResponse();
                    } else if (rpcInvocationResp.getE() != null) {
                        if (rpcInvocation.getRetry() == 0) {
                            if (retryTimes > 0) {
                                throw new TimeoutException("Wait for response from server on client " + rpcReferenceWrapper.getTimeOUt() + "ms, retry times is " + retryTimes + "Server's name is " + rpcInvocation.getTargetServiceName() + "#" + rpcInvocation.getTargetMethod());
                            }
                            return rpcInvocationResp.getE().getMessage();
                        }

                        //只有因为超时才会进行重试，否则重试不生效
                        if (nowTimeMillis - beginTime > timeout) {
                            retryTimes++;
                            //重新请求
                            rpcInvocation.clearRespAndError();
                            //每次重试的时候都将需重试次数减1
                            rpcInvocation.setRetry(rpcInvocationResp.getRetry() - 1);
                            RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
                            SEND_QUEUE.add(rpcInvocation);
                        } else {
                            throw rpcInvocationResp.getE();
                        }

                    }
                } else {
                    nowTimeMillis = System.currentTimeMillis();
                }
            }
        } finally {
            RESP_MAP.remove(rpcInvocation.getUuid());
        }
    }
}
