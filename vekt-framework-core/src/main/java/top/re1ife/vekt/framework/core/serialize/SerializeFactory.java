package top.re1ife.vekt.framework.core.serialize;

/**
 * 序列化工厂类
 */
public interface SerializeFactory {
    <T> byte[] serialize(T t);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
