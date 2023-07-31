package top.re1ife.vekt.framework.core.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.config.ClientConfig;

/**
 * @author re1ife
 * @description:
 * @date 2023/07/31 21:28:10
 * @Copyright：re1ife | blog: re1ife.top
 */
public class Client {

    private Logger logger = LoggerFactory.getLogger(Client.class);

    public static EventLoopGroup clientGroup = new NioEventLoopGroup();

    @Getter
    @Setter
    private ClientConfig clientConfig;


}

