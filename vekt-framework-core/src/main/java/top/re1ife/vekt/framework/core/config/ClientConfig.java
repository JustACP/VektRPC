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

    private long callTimeout = 3000;

    private String routerStrategy;



}
