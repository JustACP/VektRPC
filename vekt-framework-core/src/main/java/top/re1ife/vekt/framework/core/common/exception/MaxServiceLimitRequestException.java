package top.re1ife.vekt.framework.core.common.exception;

import top.re1ife.vekt.framework.core.common.RpcInvocation;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 14:25:08
 * @Copyright：re1ife | blog: re1ife.top
 */
public class MaxServiceLimitRequestException extends VektRpcException{
    public MaxServiceLimitRequestException(String message, RpcInvocation rpcInvocation) {
        super(message,rpcInvocation);
    }
}

