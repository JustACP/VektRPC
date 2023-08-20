package top.re1ife.vekt.framework.core.filter.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.ServerServiceSemaphoreWrapper;
import top.re1ife.vekt.framework.core.common.annotations.SPI;
import top.re1ife.vekt.framework.core.common.exception.MaxServiceLimitRequestException;

import java.util.concurrent.Semaphore;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 16:19:59
 * @Copyright：re1ife | blog: re1ife.top
 */
@SPI("before")
public class ServerServiceBeforeLimitFilterImpl implements IServerFilter{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServiceBeforeLimitFilterImpl.class);


    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();
        boolean tryResult = semaphore.tryAcquire();
        if (!tryResult){
            String message = String.format("[ServerServiceBeforeLimitFilterImpl#doFilter] %s's max request is %s,reject now", serviceName, serverServiceSemaphoreWrapper.getMaxNums());
            LOGGER.error(message);
            MaxServiceLimitRequestException requestException = new MaxServiceLimitRequestException(message,rpcInvocation);
            rpcInvocation.setE(requestException);
            throw requestException;
        }
    }
}
