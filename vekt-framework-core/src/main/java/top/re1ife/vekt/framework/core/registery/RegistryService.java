package top.re1ife.vekt.framework.core.registery;

/**
 * @author re1ife
 * @description: 负责服务的注册、下线、订阅、取消订阅
 * @date 2023/08/05 17:36:37
 * @Copyright：re1ife | blog: re1ife.top
 */
public interface RegistryService {


    /**
     * 注册URL，将irpc服务写入注册中心节点
     * 当网络都得的时候需要进行适当的重试做法
     * 注册服务url的时候需要写入持久化文件中
     */
    void register(URL url);

    /**
     * 服务下线，当某个服务提供者要下线了，则需要将主动将注册过的服务信息从zk指定节点上摘除
     * 此时就需要调用unRegister接口
     *
     * 持久化节点是无法进行服务下线操作的
     * 下线的服务必须保证url是完整匹配的
     * 移除持久化文件中的一些内容信息
     */
    void unRegister(URL url);

    /**
     * 消费方 订阅接口
     * 订阅某个服务： 一般是客户端在启动阶段需要调用的接口，客户端在启动过程中需要调用到该函数
     * 从注册中心中提取现有的服务提供者地址
     * @param url
     */
    void subscribe(URL url);

    /**
     * 取消订阅服务，当服务调用方不再需要订阅某些服务的时候，需要通过这个方法取消服务订阅功能
     * 将注册中心的订阅记录移除
     * @param url
     */
    void doUnSubscribe(URL url);


}
