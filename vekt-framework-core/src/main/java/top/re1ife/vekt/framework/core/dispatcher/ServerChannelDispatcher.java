package top.re1ife.vekt.framework.core.dispatcher;

import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.*;

/**
 * 请求分发器
 */
public class ServerChannelDispatcher {
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
                    executorService.submit(() -> {
                        RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                        RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);

                        //doFilter
                        SERVER_FILTER_CHAIN.doFilter(rpcInvocation);

                        //PROVIDER_CLASS_MAP就是一开始预先在启动的时候存储的Bean集合
                        Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                        Method[] methods = aimObject.getClass().getDeclaredMethods();
                        Object result = null;
                        for(Method method : methods){
                            if(method.getName().equals(rpcInvocation.getTargetMethod())){
                                try {
                                    if(method.getReturnType().equals(Void.TYPE)){
                                        method.invoke(aimObject, rpcInvocation.getArgs());
                                    }else{
                                        result = method.invoke(aimObject, rpcInvocation.getArgs());
                                    }
                                    break;
                                }catch (InvocationTargetException | IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }

                        rpcInvocation.setResponse(result);
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
