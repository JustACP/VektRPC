package top.re1ife.vekt.framework.core.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC 远程调用包装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcReferenceWrapper<T> {
    private Class<T> aimClass;

    private Map<String, Object> attatchments = new ConcurrentHashMap<>();

    public void setGroup(String group) {
        this.attatchments.put("group", group);
    }

    public void setToken(String token) {
        this.attatchments.put("token", token);
    }

    /**
     * 设置是否异步执行，默认为同步
     */
    public void setAsync(boolean async){
        this.attatchments.put("async",async);
    }

    /**
     * 是否异步执行
     */
    public boolean isAsync(){
        Object async = this.attatchments.get("async");
        if (async == null) {
            return false;
        }
        return (boolean) async;
    }


    public String getUrl() {
        return String.valueOf(attatchments.get("url"));
    }

    public void setUrl(String url) {
        attatchments.put("url", url);
    }

    public void setTimeOut(int timeOut) {
        attatchments.put("timeOut", timeOut);
    }

    public String getTimeOUt() {
        return String.valueOf(attatchments.get("timeOut"));
    }


}
