package top.re1ife.vekt.framework.core.spi.jdk;

public class DefaultISpiTest implements ISpiTest{
    @Override
    public void doSomething() {
        System.out.println("执行测试方法");
    }
}

