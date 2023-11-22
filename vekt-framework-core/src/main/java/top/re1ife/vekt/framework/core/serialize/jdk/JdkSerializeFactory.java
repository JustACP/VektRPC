package top.re1ife.vekt.framework.core.serialize.jdk;

import top.re1ife.vekt.framework.core.serialize.SerializeFactory;

import java.io.*;

public class JdkSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(t);
            outputStream.flush();
            outputStream.close();
            data = os.toByteArray();
            return data;
        }catch (IOException e){
            throw new RuntimeException("JdkSerializeFactory serialize error",e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        try {
            ObjectInputStream inputStream = new ObjectInputStream(is);
            Object result = inputStream.readObject();
            return (T) result;
        } catch (IOException e) {
            throw new RuntimeException("JdkSerializeFactory deserialize error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
