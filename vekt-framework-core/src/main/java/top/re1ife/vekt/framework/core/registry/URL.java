package top.re1ife.vekt.framework.core.registry;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;
import top.re1ife.vekt.framework.core.registry.nacos.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.SERVER_CONFIG;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URL {

    /**
     *  服务应用名称
     */
    private String applicationName;

    /**
     * 注册节点的服务名称 cn.onenine.irpc.top.re1ife.vekt.framework.core.filter.client.IClientFilter.UserService
     */
    private String serviceName;

    /**
     * Nacos group
     */
    private String groupName;


    /**
     * 这里面可以自定义不限进行扩展
     * 分组
     * 权重
     * 服务提供者的地址
     * 服务提供者的端口
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    /**
     * 将URL转换为写入zk的provider节点下的一段字符串
     */
    public static String buildProviderUrlStr(URL url) {

        return new String((url.getApplicationName() + ";" +
                url.getServiceName() + ";" + url.getGroupName() +";"+ url.getHostIp() + ":" + url.getPort() + ";" +
                System.currentTimeMillis() + ";" + url.getParameters().get("group")).getBytes(), StandardCharsets.UTF_8);
    }
    public static String buildProviderUrlStr(URL url, Double weight) {

        return new String((url.getApplicationName() + ";" +
                url.getServiceName() + ";" + url.getGroupName() +";"+ url.getHostIp() + ":" + url.getPort() + ";" +
                System.currentTimeMillis() + ";" + url.getParameters().get("group") +";"+weight).getBytes(), StandardCharsets.UTF_8);
    }

    public String getHostIp(){
        return parameters.get("host");
    }

    public int getPort(){
        return Integer.parseInt(parameters.get("port"));
    }

    public String getGroupName(){
        String groupName = parameters.get("groupName");
        if(StringUtil.isNullOrEmpty(groupName)){
            groupName = NacosConstant.DEFAULT_GROUP_NAME;
        }
        return groupName;
    }

    public static double getUrlWeight(String providerUrlStr){
        String[] items = providerUrlStr.split(";");
        return Double.parseDouble(items[6]);
    }

    /**
     * 将URL转换为写入consumer下的一段字符串
     *
     * @param url
     * @return
     */
    public static String buildConsumerUrlStr(URL url) {
        {
            String host = url.getParameters().get("host");
            return new String((url.getApplicationName() + ";" +
                    url.getServiceName() + ";" + host + ";" +
                    System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
        }
    }

    public static String buildProviderStrFromInstance(Instance instance){
        Map<String,String> map = new HashMap<>();
        map.put("host", instance.getIp());
        map.put("port", String.valueOf(instance.getPort()));
        map.put("group",instance.getMetadata().get("group"));
        URL url = new URL(instance.getMetadata().get("application"), instance.getServiceName(), NacosConstant.DEFAULT_GROUP_NAME ,map);
        return buildProviderUrlStr(url, instance.getWeight());
    }

    /**
     * 将某个节点下的信息转换为一个Provider节点对象
     */
    public static ProviderNodeInfo buildURLFromUrlStr(String providerNodeStr){
        String[] items = providerNodeStr.split(";");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[1]);
        providerNodeInfo.setGroupName(items[2]);
        providerNodeInfo.setAddress(items[3]);
        providerNodeInfo.setGroup(items[5]);
        providerNodeInfo.setWeight(Double.parseDouble(items[6]));
        return providerNodeInfo;
    }








}
