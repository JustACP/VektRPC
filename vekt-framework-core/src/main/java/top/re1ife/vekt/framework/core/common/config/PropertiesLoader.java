package top.re1ife.vekt.framework.core.common.config;


import com.alibaba.nacos.common.utils.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

    private static Properties properties;

    private static Map<String, String> propertiesMap = new HashMap<>();

    private static String DEFAULT_PROPERTIES_FILE = "/Users/kuaiyin/IdeaProjects/VektRPC/vekt-framework-core/src/main/resources/vektrpc.properties";

    public static void loadConfiguration() throws IOException {
        if(properties != null){
            return;
        }

        properties = new Properties();
        FileInputStream in = new FileInputStream(DEFAULT_PROPERTIES_FILE);
        properties.load(in);


    }

    public static String getPropertiesStr(String key) {
        if (properties == null || StringUtils.isBlank(key)) {
            return null;
        }

        if (!properties.containsKey(key)){
            throw new NullPointerException("key is null");
        }

        String value = properties.getProperty(key);

        if (!propertiesMap.containsKey(key)) {
            propertiesMap.put(key, value);
        }
        return value;
    }

    public static Integer getPropertiesInteger(String key) {
        if (properties == null || StringUtils.isBlank(key)) {
            return null;
        }

        if (!properties.containsKey(key)){
            throw new NullPointerException("key is null");
        }

        String value = properties.getProperty(key);

        if (!propertiesMap.containsKey(key)) {
            propertiesMap.put(key, value);
        }

        return Integer.valueOf(value);
    }


}
