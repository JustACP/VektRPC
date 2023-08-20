package top.re1ife.vekt.framework.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.client.Client;
import top.re1ife.vekt.framework.core.client.ConnectionHandler;
import top.re1ife.vekt.framework.core.client.RpcReference;
import top.re1ife.vekt.framework.core.client.RpcReferenceWrapper;
import top.re1ife.vekt.framework.core.common.constant.NacosConstant;
import top.re1ife.vekt.framework.interfaces.DataService;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.CLIENT_CONFIG;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/19 20:10:32
 * @Copyright：re1ife | blog: re1ife.top
 */
public class ConsumerDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemo.class);

    public static void main(String[] args) {
        try{
            Client client = new Client();

            RpcReference rpcReference = client.initClientApplication();
            RpcReferenceWrapper<DataService> dataServiceReferenceWrapper = new RpcReferenceWrapper<>();
            dataServiceReferenceWrapper.setAimClass(DataService.class);
            dataServiceReferenceWrapper.setAsync(false);
            dataServiceReferenceWrapper.setGroup("vekt_rpc");
            dataServiceReferenceWrapper.setToken("token-a");
            dataServiceReferenceWrapper.setTimeOut(Integer.parseInt(CLIENT_CONFIG.getCallTimeout()));

            DataService dataService  = rpcReference.get(dataServiceReferenceWrapper);
            client.doSubscribeService(DataService.class);
            ConnectionHandler.setBootstrap(client.getBootstrap());
            //连接所有的Provider
            client.doConnectServer();
            client.startClient();
            for (int i = 0; i < 100; i++) {
                try {
                    String result = dataService.sendData("top.re1ife.vekt.framework.core.filter.client.IClientFilter");
                    System.out.println(result);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("client error ", e);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
