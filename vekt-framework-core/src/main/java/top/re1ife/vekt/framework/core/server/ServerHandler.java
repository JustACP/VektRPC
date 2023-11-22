package top.re1ife.vekt.framework.core.server;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;
import top.re1ife.vekt.framework.core.common.cache.CommonServerCache;
import top.re1ife.vekt.framework.core.dispatcher.ServerChannelReadData;

import java.lang.reflect.Method;

import static top.re1ife.vekt.framework.core.common.cache.CommonServerCache.SERVER_CHANNEL_DISPATCHER;
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerChannelReadData serverChannelReadData = new ServerChannelReadData();
        serverChannelReadData.setChannelHandler(ctx);
        serverChannelReadData.setRpcProtocol((RpcProtocol) msg);
        //放入分发器
        SERVER_CHANNEL_DISPATCHER.add(serverChannelReadData);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();;
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }

}
