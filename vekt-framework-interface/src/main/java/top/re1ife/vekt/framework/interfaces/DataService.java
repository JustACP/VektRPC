package top.re1ife.vekt.framework.interfaces;

import java.util.List;

public interface DataService {
    /**
     * 发送数据
     * @param body 数据内容
     * @return 返回
     */
    String sendData(String body);

    /**
     * 获取数据
     * @return 返沪数据
     */
    List<String> getList();
}
