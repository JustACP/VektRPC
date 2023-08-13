package top.re1ife.vekt.framework.core.router;

import top.re1ife.vekt.framework.core.common.ChannelFutureWrapper;
import top.re1ife.vekt.framework.core.registry.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static top.re1ife.vekt.framework.core.common.cache.CommonClientCache.*;

/**
 * @author re1ife
 * @description: 随机访问方法
 * @date 2023/08/10 23:15:59
 * @Copyright：re1ife | blog: re1ife.top
 */
public class RandomRouterImpl implements VektRouter{
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[channelFutureWrappers.size()];
        //提权生成调用先后顺序的随机数组
        int[] result = createRandomIndex(arr.length);
        //按照随机数组中的数字顺序，将所有的provider channel放入新的Channel数组中
        for (int i = 0; i < result.length; i++) {
            arr[i] = channelFutureWrappers.get(result[i]);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);

    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrap(selector.getProviderServiceName());
    }

    /**
     * 将ChannelFuture 按照weight 分配并乱序
     * @param url
     */
    @Override
    public void updateWeight(URL url) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(url.getServiceName());

        //根据权重值，创建对应数组权重越大index在数组中占比达
        Integer[] weightArr = createWeightArr(channelFutureWrappers);
        Integer[] randomArr = createRandomArr(weightArr);
        ChannelFutureWrapper[] finalChannelFutureWrappers = new ChannelFutureWrapper[randomArr.length];

        for(int i = 0; i < randomArr.length; i++){
            finalChannelFutureWrappers[i] = channelFutureWrappers.get(randomArr[i]);
        }

        SERVICE_ROUTER_MAP.put(url.getServiceName(), finalChannelFutureWrappers);

    }

    private Integer[] createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers) {

        List<Integer> weightArr = new ArrayList<>();
        for(int k = 0; k < channelFutureWrappers.size(); k++){
            double weight = channelFutureWrappers.get(k).getWeight();
            int c = (int) weight / 10;
            for(int i = 0; i < c;i++){
                weightArr.add(k);
            }

        }
        Integer[] arr = new Integer[weightArr.size()];
        return weightArr.toArray(arr);

    }

    /**
     * 创建新的乱序数组
     */
    public static Integer[] createRandomArr(Integer[] arr){
        int total = arr.length;
        Random ra =  new Random();
        for(int i = 0; i < total; i++){
            int j = ra.nextInt(total);
            if(i == j){
                continue;
            }

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    private int[] createRandomIndex(int len){
        HashSet<Integer> set = new HashSet<>();
        int[] arr = new int[len];
        Random ra = new Random();

        int index  = 0;
        while(index < len){
            int num = ra.nextInt(len);
            if(!set.contains(num)){
                set.add(num);
                arr[index++] = num;
            }

        }

        return arr;

    }
}
