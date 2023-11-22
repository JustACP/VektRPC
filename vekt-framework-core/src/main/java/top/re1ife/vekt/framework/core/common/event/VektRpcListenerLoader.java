package top.re1ife.vekt.framework.core.common.event;

import com.alibaba.nacos.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.event.listener.PrvoiderNodeDataChangeListener;
import top.re1ife.vekt.framework.core.common.event.listener.ServiceDestoryListener;
import top.re1ife.vekt.framework.core.common.event.listener.ServiceUpdateListener;
import top.re1ife.vekt.framework.core.common.event.listener.VektRpcListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VektRpcListenerLoader {

    private static Logger logger = LoggerFactory.getLogger(VektRpcListener.class);

    private static List<VektRpcListener> vektRpcListeners = new ArrayList<>();

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    public static void registerListener(VektRpcListener<?> listener){
        vektRpcListeners.add(listener);
    }

    public void init(){
        registerListener(new ServiceUpdateListener());
        registerListener(new ServiceDestoryListener());
        registerListener(new PrvoiderNodeDataChangeListener());
    }

    public static Class<?> getInterfaceT(Object o){
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedTypes = (ParameterizedType) types[0];
        Type type = parameterizedTypes.getActualTypeArguments()[0];
        if(type instanceof Class<?>){
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(final VektRpcEvent vektRpcEvent){
        if(vektRpcListeners.isEmpty()){
            return;
        }

        for(final VektRpcListener vektRpcListener : vektRpcListeners){
            Class<?> type = getInterfaceT(vektRpcListener);
            if(type.equals(vektRpcEvent.getClass())){
                //当前listener监听事件
                eventThreadPool.execute(()->{

                    try {
                        vektRpcListener.callBack(vektRpcEvent.getData());
                    }catch (Exception e){
                        logger.error("sendEvent error", e);
                    }

                });
            }
        }
    }

    public static void sendSyncEvent(final VektRpcEvent vektRpcEvent){
        if(CollectionUtils.isEmpty(vektRpcListeners)){
            return;
        }

        for (final VektRpcListener vektRpcListener : vektRpcListeners) {
            Class<?> type = getInterfaceT(vektRpcListener);
            if(type.equals(vektRpcEvent.getClass())){
                try {
                    vektRpcListener.callBack(vektRpcEvent.getData());
                }catch (Exception e){
                    logger.error("sendEvent error", e);
                }

            }
        }
    }

}
