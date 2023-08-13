package top.re1ife.vekt.framework.core.common.config;

import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.config.ServerConfig;

import java.io.IOException;
import java.util.Objects;

public class PropertiesBootstrap {

    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "vektrpc.serverPort";

    public static final String REGISTER_ADDRESS = "vektrpc.registerAddr";

    public static final String APPLICATION_NAME = "vektrpc.applicationName";

    public static final String PROXY_TYPE = "vektrpc.proxyType";

    public static final String CALL_TIMEOUT = "vektrpc.call.timeout";

    public static final String ROUTE_STRATEGY = "vektrpc.routeStrategy";


    public static ServerConfig loadServerConfigFromLocal(){
        try{
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is  {}",e);
        }

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));

        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();;
        }catch (IOException e){
            throw new RuntimeException("loadClientConfigFromLocal fail,e is {}" , e);
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        clientConfig.setCallTimeout(Long.parseLong(Objects.requireNonNull(PropertiesLoader.getPropertiesStr(CALL_TIMEOUT))));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTE_STRATEGY));
        return clientConfig;
    }

}
