package top.re1ife.vekt.framework.springboot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.re1ife.vekt.framework.core.config.ClientConfig;

/**
 * @author re1ife
 * @description: Client配置
 * @date 2023/08/20 21:39:11
 * @Copyright：re1ife | blog: re1ife.top
 */
//@Configuration
//@ConfigurationProperties(prefix = "vektrpc.client")
public class RpcClientConfigProperties {
    private String applicationName;

    private String registerAddr;

    private String proxyType;

    /**
     * 接口调用超时时间
     */
    private String callTimeout = "3000";

    private String routerStrategy;

    /**
     * 序列化方式 hessian,kryo,dk,fastJson2
     */
    private String clientSerialize;

    /**
     * 注册中心类型
     */
    private String registerType;

    /**
     * 最大接收的server端响应数据大小
     */
    private Integer maxServerRespDataSize;

    public Integer getMaxServerRespDataSize() {
        return maxServerRespDataSize;
    }

    public void setMaxServerRespDataSize(Integer maxServerRespDataSize) {
        this.maxServerRespDataSize = maxServerRespDataSize;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getClientSerialize() {
        return clientSerialize;
    }

    public void setClientSerialize(String clientSerialize) {
        this.clientSerialize = clientSerialize;
    }

    public String getRouterStrategy() {
        return routerStrategy;
    }

    public void setRouterStrategy(String routeStrategy) {
        this.routerStrategy = routeStrategy;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public int getCallTimeout() {
        return Integer.parseInt(callTimeout);
    }

    public void setCallTimeout(String callTimeout) {
        this.callTimeout = callTimeout;
    }

    public void getClientConfig(){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(applicationName);
        clientConfig.setRegisterAddr(registerAddr);
        clientConfig.setProxyType(proxyType);
        clientConfig.setCallTimeout(callTimeout);
        clientConfig.setRouterStrategy(routerStrategy);
        clientConfig.setClientSerializeType(clientSerialize);
        clientConfig.setRegisterType(registerType);
        clientConfig.setMaxServerRespDataSize(maxServerRespDataSize);
    }
}
