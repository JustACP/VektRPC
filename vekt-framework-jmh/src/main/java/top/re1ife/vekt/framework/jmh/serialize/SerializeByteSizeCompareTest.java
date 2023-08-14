package top.re1ife.vekt.framework.jmh.serialize;

import top.re1ife.vekt.framework.core.serialize.SerializeFactory;
import top.re1ife.vekt.framework.core.serialize.fastjson.FastJsonSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.hessian.HessianSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.jdk.JdkSerializeFactory;
import top.re1ife.vekt.framework.core.serialize.kryo.KryoSerializeFactory;
import top.re1ife.vekt.framework.jmh.common.User;


/**
 * Description：序列化测试
 *
 * @author li.hongjian
 * @email lhj502819@163.com
 * @since 2022/12/30 21:36
 */
public class SerializeByteSizeCompareTest {

    private static User buildUserDefault() {
        User user = new User();
        user.setAge(11);
        user.setAddress("北京市昌平区");
        user.setBankNo(1215464648L);
        user.setSex(1);
        user.setId(155555);
        user.setIdCardNo("440308781129381222");
        user.setRemark("备注信息字段");
        user.setUsername("502819");
        return user;
    }

    public void jdkSerializeSizeTest(){
        SerializeFactory serializeFactory = new JdkSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        System.out.println("jdk serialize size is " + result.length);
    }
    public void hessianSerializeSizeTest(){
        SerializeFactory serializeFactory = new HessianSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        System.out.println("hessian serialize size is " + result.length);
    }
    public void kroySerializeSizeTest(){
        SerializeFactory serializeFactory = new KryoSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        System.out.println("kroy serialize size is " + result.length);
    }

    public void fastJsonSerializeSizeTest(){
        SerializeFactory serializeFactory = new FastJsonSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        System.out.println("fastJson serialize size is " + result.length);
    }

    public static void main(String[] args) {
        SerializeByteSizeCompareTest serializeByteSizeCompareTest = new SerializeByteSizeCompareTest();
        serializeByteSizeCompareTest.jdkSerializeSizeTest();
        serializeByteSizeCompareTest.hessianSerializeSizeTest();
        serializeByteSizeCompareTest.fastJsonSerializeSizeTest();
        serializeByteSizeCompareTest.kroySerializeSizeTest();
    }


}
