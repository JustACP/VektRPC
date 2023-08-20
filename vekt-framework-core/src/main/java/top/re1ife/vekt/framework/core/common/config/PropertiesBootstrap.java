package top.re1ife.vekt.framework.core.common.config;

import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.config.ServerConfig;

import java.io.IOException;
import java.util.Objects;

import static top.re1ife.vekt.framework.core.common.constant.RpcConstants.*;

public class PropertiesBootstrap {

    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "vektrpc.serverPort";

    public static final String REGISTER_ADDRESS = "vektrpc.registerAddr";

    public static final String APPLICATION_NAME = "vektrpc.applicationName";

    public static final String PROXY_TYPE = "vektrpc.proxyType";

    public static final String CALL_TIMEOUT = "vektrpc.call.timeout";

    public static final String ROUTE_STRATEGY = "vektrpc.routeStrategy";

    public static final String SERVER_SERIALIZE_TYPE="vektrpc.serverSerialize";

    public static final String CLIENT_SERIALIZE_TYPE="vektrpc.clientSerialize";

    public static final String REGISTER_TYPE = "vektrpc.registerType";

    public static final String SERVER_QUEUE_SIZE = "vektrpc.server.queue.size";

    public static final String SERVER_BIZ_THREAD_NUMS = "vektrpc.server.biz_thread_nums";

    public static final String SERVER_MAX_CONNECTION = "vektrpc.server.max.connection";

    public static final String SERVER_MAX_DATA_SIZE = "vektrpc.server.max.data.size";

    public static final String CLIENT_MAX_DATA_SIZE = "vektrpc.client.max.data.size";


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
        serverConfig.setServerSerializeType(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE_TYPE));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesInteger(SERVER_QUEUE_SIZE));
        serverConfig.setServerBizThreadNums(PropertiesLoader.getPropertiesInteger(SERVER_BIZ_THREAD_NUMS));
        serverConfig.setMaxConnections(PropertiesLoader.getPropertiesIntegerDefault(SERVER_MAX_CONNECTION,DEFAULT_MAX_CONNECTION_NUMS));
        serverConfig.setMaxServerRequestData(PropertiesLoader.getPropertiesIntegerDefault(SERVER_MAX_DATA_SIZE,SERVER_DEFAULT_MSG_LENGTH));
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
        clientConfig.setCallTimeout(Objects.requireNonNull(PropertiesLoader.getPropertiesStr(CALL_TIMEOUT)));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTE_STRATEGY));
        clientConfig.setClientSerializeType(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE_TYPE));
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        clientConfig.setMaxServerRespDataSize(PropertiesLoader.getPropertiesIntegerDefault(CLIENT_MAX_DATA_SIZE,CLIENT_DEFAULT_MSG_LENGTH));
        return clientConfig;
    }

}
