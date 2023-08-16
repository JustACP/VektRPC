package top.re1ife.vekt.framework.core.registry.nacos;

import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.event.VektRpcEvent;
import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;
import top.re1ife.vekt.framework.core.common.event.VektUpdateEvent;
import top.re1ife.vekt.framework.core.common.event.data.URLChangeWrapper;
import top.re1ife.vekt.framework.core.registry.RegistryService;
import top.re1ife.vekt.framework.core.registry.URL;

import java.util.*;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.CLIENT_CONFIG;
import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.SERVICE_LISTENER;
import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.SERVER_CONFIG;

public class NacosRegister extends AbstractRegister implements RegistryService {

    private static Logger logger = LoggerFactory.getLogger(NacosClient.class);



    private AbstractNacosClient nacosClient;

    public NacosRegister(){
        String registryAddr = CLIENT_CONFIG != null ? CLIENT_CONFIG.getRegisterAddr() :  SERVER_CONFIG.getRegisterAddr();
        this.nacosClient = new NacosClient(registryAddr);
    }

    public NacosRegister(String address){
        this.nacosClient = new NacosClient(address);
    }

    @Override
    public void Register(URL url) {
        if(!this.nacosClient.existInstance(url.getServiceName(), url.getGroupName(), url.getHostIp(), url.getPort())){
            nacosClient.registerInstance(url.getServiceName(), url.getHostIp(), url.getPort());
        }

        super.Register(url);
    }





    private void listenInstance(URL url){

        if(Objects.nonNull(SERVICE_LISTENER.get(url.getServiceName()))){
            return;
        }

        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) {
                logger.info("NacosRegister#subscribe: 监听到事件变化：{}",event);
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> changedInstance = namingEvent.getInstances();
                List<String> changedInstanceUrl = new ArrayList<>();
                for (Instance instance : changedInstance) {
                    changedInstanceUrl.add(instance.getIp() + ":" + instance.getPort());
                }
                URLChangeWrapper urlChangeWrapper = new URLChangeWrapper(namingEvent.getServiceName(), namingEvent.getGroupName(), changedInstanceUrl);

                //自定义事件监听组建
                //节点数据发生变化后，发送节点更新事件，在事件的监听端对不同行为做不同事件处理操作
                VektRpcEvent vektRpcEvent = new VektUpdateEvent(urlChangeWrapper);
                VektRpcListenerLoader.sendEvent(vektRpcEvent);


            }
        };
        SERVICE_LISTENER.put(url.getServiceName(), listener);

        nacosClient.listenService(url.getServiceName(), url.getGroupName(), listener);
    }

    @Override
    public void doUnSubscribe(URL url) {
        nacosClient.unlistenService(url.getServiceName(), url.getGroupName(), SERVICE_LISTENER.get(url.getServiceName()));
        super.doUnSubscribe(url);
    }

    @Override
    public void doAfterSubscribe(URL url) {
        listenInstance(url);
    }

    @Override
    public List<String> getProviderIps(String serviceName) {

        List<Instance> allServiceInstance = nacosClient.getAllServiceInstance(serviceName);
        StringBuilder ipBuilder = new StringBuilder();
        List<String> providerIps = new ArrayList<>();
        for (Instance instance : allServiceInstance) {
            ipBuilder.append(instance.getIp()).append(":").append(instance.getPort());
            providerIps.add(ipBuilder.toString());
            ipBuilder.delete(0, ipBuilder.length());
        }
        return providerIps;

    }

    @Override
    public Map<String, Double> getServiceWeightMap(String serviceName) {
        List<Instance> allServiceInstance = nacosClient.getAllServiceInstance(serviceName);
        StringBuilder ipAndPortBuilder = new StringBuilder();
        Map<String, Double> result = new HashMap<>();
        for (Instance instance : allServiceInstance) {
            ipAndPortBuilder.append(instance.getIp()).append(":").append(instance.getPort());
            result.put(ipAndPortBuilder.toString(), instance.getWeight());
            ipAndPortBuilder.delete(0, ipAndPortBuilder.length());
        }

        return result;
    }


}
