package top.re1ife.vekt.framework.core.registry.nacos;

import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Map;

public abstract class AbstractNacosClient {

    private String nacosAddress;

    public AbstractNacosClient(String nacosAddress){
        this.nacosAddress = nacosAddress;
    }




    public abstract Object getClient();

    /**
     * 拉取节点的数据
     */
    public abstract Instance getServiceInstance(String serviceName, String ip, String port);


    public abstract Instance getServiceInstance(String serviceName, String ip, int port);

    public abstract Instance getServiceInstance(String serviceName, String groupName, String ip, int port);

    public abstract List<Instance> getAllServiceInstance(String serviceName);

    public abstract List<Instance> getAllServiceInstance(String serviceName, String groupName);

    public abstract void registerInstance(String serviceName, String groupName, String ip, int port);

    public abstract void registerInstance(String serviceName, String ip, int port, Map<String, String> metadata);

    public abstract void deregisterInstance(String serviceName, String groupName, String ip, int port);

    public abstract void registerInstance(String serviceName, String ip, int port);

    public abstract void deregisterInstance(String serviceName, String ip, int port);

    public abstract boolean existInstance(String serviceName, String groupName, String ip, int port);

    public abstract boolean existInstance(String serviceName, String ip, int port);

    /**
     * 监听某个服务下的某个节点的变化
     */

    public abstract void listenService(String serviceName, EventListener listener);

    public abstract void listenService(String serviceName, String groupName, EventListener listener);

    public abstract void delistenService(String serviceName, String groupName, EventListener listener);

    public abstract void unlistenService(String serviceName, String groupName, EventListener listener);

    public abstract void unlistenService(String serviceName, EventListener listener);
}
