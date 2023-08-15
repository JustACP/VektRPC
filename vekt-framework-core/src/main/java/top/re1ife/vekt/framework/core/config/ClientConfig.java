package top.re1ife.vekt.framework.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientConfig {

    private String registerAddr;

    private String applicationName;


    private String proxyType;

    private String callTimeout = "3000";

    private String routerStrategy;

    private String clientSerializeType;

    /**
     * 注册中心类型
     */
    private String registerType;



}
