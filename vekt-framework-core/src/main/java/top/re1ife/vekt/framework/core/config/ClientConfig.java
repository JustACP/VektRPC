package top.re1ife.vekt.framework.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientConfig {


    private Integer port;

    private String serverAddr;



}
