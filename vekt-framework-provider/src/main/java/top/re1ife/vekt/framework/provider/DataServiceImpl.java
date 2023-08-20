package top.re1ife.vekt.framework.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.re1ife.vekt.framework.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

public class DataServiceImpl implements DataService {
    private Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);
    @Override
    public String sendData(String body) {
        logger.info("已收到参数长度 : {}", body.length());

        return "success";
    }

    @Override
    public List<String> getList() {
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("nine 9");
        arrayList.add("nine 9");
        arrayList.add("nine 9");
        return arrayList;
    }
}
