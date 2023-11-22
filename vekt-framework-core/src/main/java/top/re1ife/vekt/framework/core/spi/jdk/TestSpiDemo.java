package top.re1ife.vekt.framework.core.spi.jdk;

import java.util.Iterator;
import java.util.ServiceLoader;

public class TestSpiDemo {
    public static void doTest(ISpiTest iSpiTest){
        System.out.println("begin");
        iSpiTest.doSomething();
        System.out.println("end");
    }
    public static void main(String[] args) {
        ServiceLoader<ISpiTest> serviceLoader = ServiceLoader.load(ISpiTest.class);
        Iterator<ISpiTest> iSpiTestIterator = serviceLoader.iterator();
        while (iSpiTestIterator.hasNext()) {
            ISpiTest iSpiTest = iSpiTestIterator.next();
            TestSpiDemo.doTest(iSpiTest);
        }
    }
}
