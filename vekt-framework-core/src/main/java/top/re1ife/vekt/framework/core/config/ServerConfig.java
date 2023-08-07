package top.re1ife.vekt.framework.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerConfig {
    private int port;

    private String applicationName;

    private String registerAddr;
}
