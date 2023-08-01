package top.re1ife.vekt.framework.core.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonServerCache {

    public static Map<String,Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();
}
