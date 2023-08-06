package top.re1ife.vekt.framework.core.common.cache;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.config.ClientConfig;
import top.re1ife.vekt.framework.core.registery.URL;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class CommonServerCache {

    public static Map<String,Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();

    public static Set<URL> PROVIDER_URL_SET = new CopyOnWriteArraySet<>();


}
