package top.re1ife.vekt.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static top.re1ife.vekt.framework.core.common.constants.RpcConstants.MAGIC_NUMBER;

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
            if(byteBuf.readableBytes() > 1000){
                //防止收到体积郭大的数据包
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            int beginReader;

            while(true){
                beginReader = byteBuf.readerIndex();
                byteBuf.markReaderIndex();
                if(byteBuf.readShort() == MAGIC_NUMBER){
                    break;
                }else{
                    //如果不是魔数开头，说明是非法的客户端发来的数据包
                    channelHandlerContext.close();
                    return;
                }
            }

            int contentLength = byteBuf.readInt();

            if(byteBuf.readableBytes() < contentLength){
                byteBuf.readerIndex(beginReader);
                return;
            }

            //这里其实就是实际的RpcProtocol对象的content字段
            byte[] data = new byte[contentLength];
            byteBuf.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);
            list.add(rpcProtocol);
        }
    }
}
