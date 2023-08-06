package top.re1ife.vekt.framework.core.registery.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.client.naming.NacosNamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/06 00:12:59
 * @Copyright：re1ife | blog: re1ife.top
 */
public class VektNacosClient extends AbstractNacosClient{

    private Logger logger = LoggerFactory.getLogger(VektNacosClient.class);

    private NamingService nacosNamingService;



    public VektNacosClient(String address) {
        super(address);
        try {

            this.nacosNamingService = NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            logger.error("VektNacosClient#VektNacosClient: Build Naming Factory Error!");
            throw new RuntimeException(e);
        }

    }




    @Override
    public Object getClient() {
        return null;
    }



    @Override
    public void destroy() {
        try {
            nacosNamingService.shutDown();
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }


}
