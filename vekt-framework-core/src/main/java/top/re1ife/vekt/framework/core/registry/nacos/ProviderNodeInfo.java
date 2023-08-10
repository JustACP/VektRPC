package top.re1ife.vekt.framework.core.registry.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * provider 节点信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderNodeInfo {

    private String serviceName;

    private String groupName;

    private String address;




}
