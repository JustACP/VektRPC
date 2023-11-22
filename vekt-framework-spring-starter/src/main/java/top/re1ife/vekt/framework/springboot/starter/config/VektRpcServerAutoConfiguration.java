package top.re1ife.vekt.framework.springboot.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.re1ife.vekt.framework.core.server.ApplicationShutdownHook;
import top.re1ife.vekt.framework.core.server.Server;
import top.re1ife.vekt.framework.core.server.ServiceWrapper;
import top.re1ife.vekt.framework.springboot.starter.common.VektRpcService;

import java.util.Map;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 21:41:47
 * @Copyright：re1ife | blog: re1ife.top
 */
public class VektRpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(VektRpcServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = null;
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(VektRpcService.class);
        if (beansWithAnnotation.isEmpty()) {
            //没有带暴露的服务
            return;
        }

        printBanner();
        long start = System.currentTimeMillis();
        server = new Server();
        server.initServerConfig();
        for (String beanName : beansWithAnnotation.keySet()) {
            Object beanObject = beansWithAnnotation.get(beanName);
            VektRpcService iRpcService = beanObject.getClass().getAnnotation(VektRpcService.class);
            ServiceWrapper serviceWrapper = new ServiceWrapper(beanObject, iRpcService.group());
            serviceWrapper.setLimit(iRpcService.limit());
            serviceWrapper.setServiceToken(iRpcService.serviceToken());
            server.exportService(serviceWrapper);
            LOGGER.info("service {} export success!", beanName);
        }
        long end = System.currentTimeMillis();
        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
        LOGGER.info("{} start success in {} times", server.getConfig(), end - start);
    }

    private void printBanner(){
        System.out.println("VektRpc Start!");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
