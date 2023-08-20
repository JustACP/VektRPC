package top.re1ife.vekt.framework.core.filter.client;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.Iterator;
import java.util.List;

public class DirectInvokeFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = (String) rpcInvocation.getAttachments().get("url");
        if(StringUtils.isBlank(url)){
            return;
        }

        Iterator<ChannelFutureWrapper> iterator = src.iterator();
        while(iterator.hasNext()){
            ChannelFutureWrapper channelFutureWrapper = iterator.next();
            if(!(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()).equals(url)){
                iterator.remove();
            }
        }
        if(CollectionUtils.isEmpty(src)){
            throw new RuntimeException("no match for url: " + url);
        }
    }
}
