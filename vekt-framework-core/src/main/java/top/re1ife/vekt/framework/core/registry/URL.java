package top.re1ife.vekt.framework.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.re1ife.vekt.framework.core.registry.nacos.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URL {

    /**
     *  服务应用名称
     */
    private String applicationName;

    /**
     * 注册节点的服务名称 cn.onenine.irpc.test.UserService
     */
    private String serviceName;

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
        String host = url.getParameters().get("host");
        String port = url.getParameters().get("port");
        return new String((url.getApplicationName() + ";" +
                url.getServiceName() + ";" + host + ":" + port + ";" +
                System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
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

    /**
     * 将某个节点下的信息转换为一个Provider节点对象
     */
    public static ProviderNodeInfo buildURLFromUrlStr(String providerNodeStr){
        String[] items = providerNodeStr.split("/");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[2]);
        providerNodeInfo.setAddress(items[4]);
        return providerNodeInfo;
    }




}
