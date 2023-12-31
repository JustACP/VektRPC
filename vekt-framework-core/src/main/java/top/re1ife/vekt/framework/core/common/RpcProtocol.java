package top.re1ife.vekt.framework.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.re1ife.vekt.framework.core.common.constant.RpcConstants;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 自定义序列化协议
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 8036047420171733802L;

    /**
     * 魔法数 主要在做服务通讯的时候定义的一个安全监测，确认当前请求的协议是否合法
     */
    private short magicNumber = RpcConstants.MAGIC_NUMBER;

    /**
     * 协议传输核心数据的长度，这里将长度单独拎出来有个好处，
     *  当服务端的接收能力有限的时候，可以对该字段进行赋值。
     *  当读取到网络数据包中的contentLength字段已经超过预期值的话，就不会去读取content字段
     */
    private int contentLength;

    /**
     * 核心传输数据，这里核心的传输数据主要是请求的服务名称，请求服务的方法名称，请求参数内容。
     *  为了方便后期扩展，这些核心的请求数据都统一封装到了RpcInvocation
     */
    private byte[] content;

    public RpcProtocol(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }

}
