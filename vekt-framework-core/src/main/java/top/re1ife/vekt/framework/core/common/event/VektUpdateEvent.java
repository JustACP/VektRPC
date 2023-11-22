package top.re1ife.vekt.framework.core.common.event;

/**
 * 节点更新事件
 */
public class VektUpdateEvent implements VektRpcEvent{

    private Object data;

    public VektUpdateEvent(Object data){
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public VektRpcEvent sendData(Object data) {
        this.data = data;
        return this;
    }
}
