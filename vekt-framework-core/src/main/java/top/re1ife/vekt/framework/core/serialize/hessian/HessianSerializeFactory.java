package top.re1ife.vekt.framework.core.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import top.re1ife.vekt.framework.core.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output();
            output.writeObject(t);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            data =  os.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("HessianSerializeFactory serialize error", e);
        }

        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if(data == null){
            return null;
        }

        Object result = null;
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        Hessian2Input input  = new Hessian2Input(is);
        try {
            result = input.readObject();
        } catch (IOException e) {
            throw new RuntimeException("HessianSerializeFactory deSerialize error", e);
        }

        return (T) result;
    }
}
