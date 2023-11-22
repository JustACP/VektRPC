package top.re1ife.vekt.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.MathContext;
import java.util.List;

import static top.re1ife.vekt.framework.core.common.constant.RpcConstants.MAGIC_NUMBER;

/**
 * RPC 解码器 所有数据都入站要过解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * 协议开头部分标注长度
     */
    public final int BASE_LENGTH  = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() >= BASE_LENGTH){

            if((byteBuf.readShort() != MAGIC_NUMBER)){
                channelHandlerContext.close();
                return;
            }

            int length = byteBuf.readInt();

            //说明剩余的数据包不是完整的，这里需要重置下readerIndex
            if (byteBuf.readableBytes() < length) {
                channelHandlerContext.close();
                return;
            }



            //这里其实就是实际的RpcProtocol对象的content字段
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);
            list.add(rpcProtocol);
        }
    }
}
