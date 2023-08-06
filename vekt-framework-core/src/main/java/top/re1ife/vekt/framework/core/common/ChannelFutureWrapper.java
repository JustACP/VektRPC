package top.re1ife.vekt.framework.core.common;

import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author re1ife
 * @description: 服务对应的ChannelFuture
 * @date 2023/08/05 20:26:00
 * @Copyright：re1ife | blog: re1ife.top
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelFutureWrapper {
    private ChannelFuture channelFuture;

    private String host;
    private String port;
}
