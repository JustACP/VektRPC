package top.re1ife.vekt.framework.core.registery.nacos;

/**
 * @author re1ife
 * @description: Provider节点信息
 * @date 2023/08/03 20:37:14
 * @Copyright：re1ife | blog: re1ife.top
 */
public class ProviderNodeInfo {

    private String serviceName;

    private String address;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
