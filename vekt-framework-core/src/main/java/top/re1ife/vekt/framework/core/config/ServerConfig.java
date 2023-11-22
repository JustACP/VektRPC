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

    private String serverSerializeType;

    /**
     * 注册中心类型
     */
    private String registerType;

    /**
     * 任务队列大小
     */
    private Integer serverQueueSize;

    /**
     * 处理业务线程数
     */
    private Integer serverBizThreadNums;

    /**
     * 限制服务端最大所能接受的数据包体积
     */
    private Integer maxServerRequestData;

    /**
     * 服务端最大连接数
     */
    private Integer maxConnections;
}
