package top.re1ife.vekt.framework.core.serialize.fastjson;

import com.alibaba.fastjson2.JSONObject;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;

public class FastJsonSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        return JSONObject.toJSONString(t).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSONObject.parseObject(new String(data), clazz);
    }
}
