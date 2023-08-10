package top.re1ife.vekt.framework.core.common.event.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;

import java.util.List;

@Data
@NoArgsConstructor
public class URLChangeWrapper {
    private String serviceName;

    private String groupName;

    private List<String> providerUrl;

    public URLChangeWrapper(String serviceName, String groupName, List<String> providerUrl) {
        this.serviceName = serviceName;
        this.groupName = groupName;
        this.providerUrl = providerUrl;
    }

    public URLChangeWrapper(String serviceName, List<String> providerUrl) {
        new URLChangeWrapper(serviceName, NacosConstant.DEFAULT_GROUP_NAME, providerUrl);
    }


    @Override
    public String toString() {
        return "URLChangeWrapper{" +
                "serviceName='" + serviceName + '\'' +
                ", providerUrl=" + providerUrl +
                '}';
    }

}
