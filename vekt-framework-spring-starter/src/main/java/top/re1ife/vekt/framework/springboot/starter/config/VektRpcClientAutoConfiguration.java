package top.re1ife.vekt.framework.springboot.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import top.re1ife.vekt.framework.core.client.Client;
import top.re1ife.vekt.framework.core.client.ConnectionHandler;
import top.re1ife.vekt.framework.core.client.RpcReference;
import top.re1ife.vekt.framework.core.client.RpcReferenceWrapper;
import top.re1ife.vekt.framework.springboot.starter.common.VektRpcReference;

import java.lang.reflect.Field;

/**
 * @author re1ife
 * @description: Server端自动装配
 * @date 2023/08/20 21:49:33
 * @Copyright：re1ife | blog: re1ife.top
 */
public class VektRpcClientAutoConfiguration implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(VektRpcServerAutoConfiguration.class);

    private Client client;

    private RpcReference rpcReference;

    /**
     * 是否需要启动NettyClient
     */
    private boolean hasInitClientApplication;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(applicationContext);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (hasInitClientApplication){
            ConnectionHandler.setBootstrap(client.getBootstrap());
            client.doConnectServer();
            client.startClient();
        }
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                //设置私有变量可访问
                field.setAccessible(true);
                VektRpcReference rpcReferenceAnnotation = field.getAnnotation(VektRpcReference.class);
                if (rpcReferenceAnnotation == null) {
                    continue;
                }
                if (!hasInitClientApplication){
                    try {
                        client = new Client();
                        rpcReference = client.initClientApplication();
                    } catch (Exception e) {
                        LOGGER.error("init and start netty client error" , e);
                        throw new RuntimeException(e);
                    }
                }
                hasInitClientApplication = true;
                RpcReferenceWrapper rpcReferenceWrapper = new RpcReferenceWrapper();
                rpcReferenceWrapper.setAimClass(field.getType());
                rpcReferenceWrapper.setGroup(rpcReferenceAnnotation.group());
                rpcReferenceWrapper.setTimeOut(rpcReferenceAnnotation.timeOut());
                rpcReferenceWrapper.setToken(rpcReferenceAnnotation.serviceToken());
                rpcReferenceWrapper.setUrl(rpcReferenceAnnotation.url());
                rpcReferenceWrapper.setRetry(rpcReferenceAnnotation.retry());
                rpcReferenceWrapper.setAsync(rpcReferenceAnnotation.async());
                field.set(bean,rpcReference.get(rpcReferenceWrapper));
                //订阅服务，提前获取到所有的service Provider
                client.doSubscribeService(field.getType());
            } catch (Throwable e) {
                throw new RuntimeException("[IRpcClientAutoConfiguration#postProcessAfterInitialization] init rpcReference error", e);
            }
        }
        return bean;
    }
}
