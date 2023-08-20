package top.re1ife.vekt.framework.provider;

import top.re1ife.vekt.framework.core.common.event.VektRpcListenerLoader;
import top.re1ife.vekt.framework.core.server.ApplicationShutdownHook;
import top.re1ife.vekt.framework.core.server.Server;
import top.re1ife.vekt.framework.core.server.ServiceWrapper;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/19 19:41:30
 * @Copyright：re1ife | blog: re1ife.top
 */
public class ProviderDemo {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.initServerConfig();
            VektRpcListenerLoader vektRpcListenerLoader = new VektRpcListenerLoader();
            vektRpcListenerLoader.init();
            ServiceWrapper dataServiceWrapper = new ServiceWrapper(new DataServiceImpl(),"top.re1ife.vekt.framework.core.filter.client.IClientFilter");
            dataServiceWrapper.setServiceToken("token-a");
            dataServiceWrapper.setLimit(2);
            server.exportService(dataServiceWrapper);
            ServiceWrapper userServiceWrapper = new ServiceWrapper(new UserServiceImpl(),"dev");
            userServiceWrapper.setServiceToken("token-b");
            userServiceWrapper.setLimit(2);
            server.exportService(userServiceWrapper);
            //注册destroy钩子函数
            ApplicationShutdownHook.registryShutdownHook();
            server.startApplication();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
