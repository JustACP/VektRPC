package top.re1ife.vekt.framework.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcInvocation {
    /**
     * 请求目标的方法 如getInfo
     */
    private String targetMethod;

    /**
     * 请求目标服务名称 如: top.re1ife.user.userService
     */
    private String targetServiceName;

    /**
     * 请求参数信息
     */
    private Object[] args;

    /**
     * 用于匹配请求与响应的关键值，从客户端发出请求时会有一个uuid用于记录发出的请求
     *
     * 数据返回时，通过UUID匹配对应线程，且返回给对应线程
     */
    private String uuid;

    /**
     * 接口响应的数据塞入这个字段中(如果是异步调用或者是void类型，这里就为空)
     */
    private Object response;

    @Override
    public String toString() {
        return "RpcInvocation{" +
                "targetMethod='" + targetMethod + '\'' +
                ", targetServiceName='" + targetServiceName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", uuid='" + uuid + '\'' +
                ", response=" + response +
                '}';
    }
}
