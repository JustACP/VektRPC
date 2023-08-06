package top.re1ife.vekt.framework.core.registery;

import top.re1ife.vekt.framework.core.registery.nacos.ProviderNodeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author re1ife
 * @description: 配置总线 将VektRPC主要配置封装好，重要配置基于这个类存储
 * @date 2023/08/03 20:37:14
 * @Copyright：re1ife | blog: re1ife.top
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class URL {
    /**
     * 服务应用名称
     */
    private String applicationName;

    /**
     * 注册节点的服务名称
     */
    private String serviceName;

    /**
     * 自定义无限扩展
     * 分组
     * 权重
     * 服务提供者地址
     * 服务提供者端口
     */
    private Map<String, String> parameter = new HashMap<>();

    /**
     * 将URL转换为写入consumer下的一段字符串
     *
     * @param url
     * @return
     */
    public static String buildConsumerUrlStr(URL url) {
        {
            String host = url.getParameter().get("host");
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
