package top.re1ife.vekt.framework.core.server;

import com.alibaba.fastjson2.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;
import top.re1ife.vekt.framework.core.common.cache.CommonServerCache;

import java.lang.reflect.Method;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将接收到的信息以 RpcProtocol 协议解析
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String json = new String(rpcProtocol.getContent(), 0 , rpcProtocol.getContentLength());
        RpcInvocation rpcInvocation = JSONObject.parseObject(json,RpcInvocation.class);

        //PROVIDER_CLASS_MAP 就是一开始预先启动时存储的Bean集合
        Object aimObject = CommonServerCache.PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
        Method[] methods = aimObject.getClass().getMethods();
        Object result = null;
        for(Method method : methods){
            if(method.getName().equals(rpcInvocation.getTargetMethod())){
                if(method.getReturnType().equals(Void.TYPE)){
                    method.invoke(aimObject, rpcInvocation.getArgs());
                    logger.info("ServerHandler#channelRead: RPC No Return Method invoked");
                }else{
                    result = method.invoke(aimObject, rpcInvocation.getArgs());
                    logger.info("ServerHandler#channelRead: RPC Method Invoked args: {}, result: {}", JSONObject.toJSONString(rpcInvocation.getArgs()), JSONObject.toJSONString(result));

                }
                break;
            }
        }

        rpcInvocation.setResponse(result);
        RpcProtocol respRpcProtocol = new RpcProtocol(JSONObject.toJSONString(rpcInvocation).getBytes());
        ctx.writeAndFlush(respRpcProtocol);
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
