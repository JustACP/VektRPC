package top.re1ife.vekt.framework.core.dispatcher;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;
import top.re1ife.vekt.framework.core.common.exception.VektRpcException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.*;

/**
 * 请求分发器
 */
public class ServerChannelDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelDispatcher.class);
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;

    private ExecutorService executorService;

    public void init(int queueSize, int bizThreadNum){
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(bizThreadNum, bizThreadNum, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(512));

    }

    public void add(ServerChannelReadData serverChannelReadData){
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    public ServerChannelDispatcher(){

    }

    class ServerJobCoreHandle implements Runnable{
        @Override
        public void run() {
            while(true){
                try{
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    logger.info("msg 收到");
                    executorService.submit(() -> {
                        RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                        RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
                        logger.info(JSONObject.toJSONString(rpcInvocation));
                        //doFilter
                        try {
                            //doBeforeFilter 前置过滤器
                            SERVER_BEFORE_FILTER_CHAIN.doFilter(rpcInvocation);
                        } catch (Exception e) {
                            //针对自定义异常进行处理
                            if (e instanceof VektRpcException){
                                logger.error("ServerChannelDispatcher#serverJobCore: filter error", e);
                                VektRpcException rpcException = (VektRpcException) e;
                                RpcInvocation reqParam = rpcException.getRpcInvocation();

                                byte[] body = SERVER_SERIALIZE_FACTORY.serialize(reqParam);
                                RpcProtocol respRpcProtocol = new RpcProtocol(body);
                                serverChannelReadData.getChannelHandler().writeAndFlush(respRpcProtocol);
                                return;
                            }
                        }


                        //PROVIDER_CLASS_MAP就是一开始预先在启动的时候存储的Bean集合
                        Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                        Method[] methods = aimObject.getClass().getDeclaredMethods();
                        Object result = null;
                        for(Method method : methods){
                            if(method.getName().equals(rpcInvocation.getTargetMethod())){
                                if(method.getReturnType().equals(Void.TYPE)){
                                    try {
                                        method.invoke(aimObject, rpcInvocation.getArgs());
                                    }catch (Exception e){
                                        logger.error("ServerChannelDispatcher#serverJobCore: call error", e);
                                        rpcInvocation.setE(e);
                                    }

                                }else{
                                    try{
                                        result = method.invoke(aimObject, rpcInvocation.getArgs());
                                    }catch (Exception e){
                                        logger.error("ServerChannelDispatcher#serverJobCore: call error", e);
                                        rpcInvocation.setE(e);
                                    }

                                }
                                break;

                            }
                        }
                        boolean isAsync = (boolean) rpcInvocation.getAttachments().get("async");
                        if (isAsync){
                            //如果是异步请求则不用返回结结果，减少网络传输
                            return;
                        }

                        rpcInvocation.setResponse(result);
                        //doAfterFilter 后置处理器
                        SERVER_AFTER_FILTER_CHAIN.doFilter(rpcInvocation);
                        RpcProtocol respRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                        serverChannelReadData.getChannelHandler().writeAndFlush(respRpcProtocol);
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void startDataConsume(){
        Thread thread = new Thread(new ServerJobCoreHandle());
        thread.start();
    }

}
