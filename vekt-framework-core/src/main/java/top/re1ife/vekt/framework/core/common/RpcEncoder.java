package top.re1ife.vekt.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author re1ife
 * @description: Rpc 编码器 所有信息出站会经过这
 * @date 2023/07/31 20:23:25
 * @Copyright：re1ife | blog: re1ife.top
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol rpcProtocol, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(rpcProtocol.getMagicNumber());
        byteBuf.writeInt(rpcProtocol.getContentLength());
        byteBuf.writeBytes(rpcProtocol.getContent());
    }
}
