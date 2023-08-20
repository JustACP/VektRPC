package top.re1ife.vekt.framework.core.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NacosClient extends AbstractNacosClient {

    private static Logger logger = LoggerFactory.getLogger(NacosClient.class);

    private NamingService namingService;

    public NacosClient(String nacosAddress)  {
        super(nacosAddress);

        try{
            this.namingService = NamingFactory.createNamingService(nacosAddress);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }


    }



    @Override
    public Object getClient() {
        return namingService;
    }

    @Override
    public Instance getServiceInstance(String serviceName, String ip, String port) {
        return getServiceInstance(serviceName, ip, Integer.parseInt(port));
    }

    @Override
    public Instance getServiceInstance(String serviceName, String ip, int port){
        return getServiceInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME, ip, port);
    }

    @Override
    public Instance getServiceInstance(String serviceName, String groupName,String ip, int port){
        List<Instance> allServiceInstance = getAllServiceInstance(serviceName,groupName);
        for (Instance instance : allServiceInstance) {
            if(instance.getIp().equals(ip) && instance.getPort() == port){
                return instance;
            }
        }
        logger.warn("NacosClient#getServiceIntance: there is no match instance serviceName: {}, ip: {}, port: {}",serviceName, ip, port);
        return null;
    }

    @Override
    public List<Instance> getAllServiceInstance(String serviceName) {
        return getAllServiceInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME);
    }

    @Override
    public List<Instance> getAllServiceInstance(String serviceName, String groupName) {
        try {
            return namingService.getAllInstances(serviceName, groupName);
        } catch (NacosException e) {
            logger.error("NacosClient#getAllServiceInstance: serviceName: {} NacosException: {}", serviceName, e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerInstance(String serviceName, String groupName, String ip, int port){
        registerInstance(serviceName,groupName,ip,port,null);
    }


    public void registerInstance(String serviceName, String groupName, String ip, int port, Map<String,String> metadata){
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(port);
        instance.setServiceName(serviceName);
        instance.setMetadata(metadata);
        try {
            namingService.registerInstance(serviceName,groupName ,instance);
        } catch (NacosException e) {
            logger.error("NacosClient#registerInstace: serviceName: {} groupName: {} ip: {} port: {} register instance error", serviceName, groupName, ip, port);
            throw new RuntimeException(e);
        }
    }
    @Override
    public void registerInstance(String serviceName, String ip, int port){
        registerInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME , ip, port);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, Map<String, String> metadata){
        registerInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME , ip, port, metadata);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, String ip, int port){
        try {
            namingService.deregisterInstance(serviceName, groupName, ip, port);
        } catch (NacosException e) {
            logger.error("NacosClient#registerInstace: serviceName: {} groupName: {} ip: {} port: {} register instance error", serviceName, groupName, ip, port);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port){
        deregisterInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME , ip, port);
    }

    @Override
    public boolean existInstance(String serviceName,String groupName ,String ip, int port){
        Instance serviceInstance = getServiceInstance(serviceName, groupName, ip, port);
        return Objects.nonNull(serviceInstance);
    }


    @Override
    public boolean existInstance(String serviceName,String ip, int port){
        Instance serviceInstance = getServiceInstance(serviceName, NacosConstant.DEFAULT_GROUP_NAME, ip, port);
        return Objects.nonNull(serviceInstance);
    }



    @Override
    public void listenService(String serviceName, EventListener listener) {
        listenService(serviceName, NacosConstant.DEFAULT_GROUP_NAME, listener);
    }

    @Override
    public void listenService(String serviceName, String groupName, EventListener listener){
        try {

            namingService.subscribe(serviceName, groupName,listener);
        } catch (NacosException e) {
            logger.error("NacosClient#listenService listen service error serviceName: {} groupName: {} Eventlistener: {}", serviceName, groupName, listener);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delistenService(String serviceName, String groupName, EventListener listener) {

    }

    @Override
    public void unlistenService(String serviceName, String groupName, EventListener listener){
        try {
            namingService.unsubscribe(serviceName, groupName, listener);
        } catch (NacosException e) {
            logger.error("NacosClient#listenService unlisten service error serviceName: {} groupName: {} Eventlistener: {}", serviceName, groupName, listener);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unlistenService(String serviceName, EventListener listener){

        unlistenService(serviceName, NacosConstant.DEFAULT_GROUP_NAME, listener);

    }






}
