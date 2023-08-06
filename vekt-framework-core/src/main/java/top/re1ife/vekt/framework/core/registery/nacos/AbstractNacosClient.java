package top.re1ife.vekt.framework.core.registery.nacos;

import com.alibaba.nacos.api.naming.listener.EventListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/06 00:01:52
 * @Copyright：re1ife | blog: re1ife.top
 */
@Data
public abstract class AbstractNacosClient {

    public AbstractNacosClient(String SERVER_ADDR) {
        this.SERVER_ADDR = SERVER_ADDR;
    }

    private String SERVER_ADDR;






    public abstract Object getClient();



    /**
     * 断开的客户端连接
     */
    public abstract void destroy();

}
