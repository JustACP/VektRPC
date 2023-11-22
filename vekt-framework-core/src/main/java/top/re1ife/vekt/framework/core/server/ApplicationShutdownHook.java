package top.re1ife.vekt.framework.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.event.VektRpcDestoryEvent;
import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;

/**
 * 用来监听Java进程关闭
 */
public class ApplicationShutdownHook {

    public static Logger logger = LoggerFactory.getLogger(ApplicationShutdownHook.class);

    public static void registryShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("[ApplicationShutdownHook#registryShutdownHook]..");
            VektRpcListenerLoader.sendSyncEvent(new VektRpcDestoryEvent("application destroy"));
        }));
    }

}
