package top.re1ife.vekt.framework.core.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;
import top.re1ife.vekt.framework.core.registry.nacos.NacosClient;

/**
 * 服务包装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceWrapper {

    /**
     * 对外暴露的具体服务对象
     */
    private Object serverObj;

    private String groupName = NacosConstant.DEFAULT_GROUP_NAME;

    public ServiceWrapper(Object serverObj) {
        this.serverObj = serverObj;
    }
}
