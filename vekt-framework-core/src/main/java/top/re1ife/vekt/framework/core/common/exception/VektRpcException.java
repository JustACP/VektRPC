package top.re1ife.vekt.framework.core.common.exception;

import lombok.Getter;
import lombok.Setter;
import top.re1ife.vekt.framework.core.common.RpcInvocation;
import top.re1ife.vekt.framework.core.common.RpcProtocol;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 14:23:10
 * @Copyright：re1ife | blog: re1ife.top
 */
public class VektRpcException extends RuntimeException {

    @Getter
    @Setter
    private RpcInvocation rpcInvocation;

    private String message;

    public VektRpcException(RpcInvocation rpcInvocation){
        this.rpcInvocation = rpcInvocation;
    }

    public VektRpcException(String message, RpcInvocation rpcInvocation){
        super(message);
        this.rpcInvocation = rpcInvocation;
    }


}
