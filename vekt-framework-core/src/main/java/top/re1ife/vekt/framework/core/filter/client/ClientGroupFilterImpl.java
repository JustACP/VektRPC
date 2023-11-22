package top.re1ife.vekt.framework.core.filter.client;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.common.RpcInvocation;

import java.util.Iterator;
import java.util.List;

public class ClientGroupFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = (String) rpcInvocation.getAttachments().get("group");
        if(StringUtils.isBlank(group)){
            return;
        }

        Iterator<ChannelFutureWrapper> iterator = src.iterator();
        while(iterator.hasNext()){
            ChannelFutureWrapper next = iterator.next();
            if(!next.getGroup().equals(group)){
                iterator.remove();
            }
        }

        if(CollectionUtils.isEmpty(src)){
            throw new RuntimeException("no provider match for group " + group);
        }
    }
}
