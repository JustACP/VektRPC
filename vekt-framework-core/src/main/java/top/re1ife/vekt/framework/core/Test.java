package top.re1ife.vekt.framework.core;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/06 16:39:11
 * @Copyright：re1ife | blog: re1ife.top
 */
public class Test {

    public static void main(String[] args) throws NacosException {
        NamingService namingService = NamingFactory.createNamingService("swpu.52codes.top:8848");
        String serviceName = "your_service_name"; // 服务名
        String ip = "127.0.0.1"; // 服务实例的 IP 地址
        int port = 8080; // 服务实例的端口号
        namingService.registerInstance(serviceName, ip, port);
        while(true){

        }
    }
}
