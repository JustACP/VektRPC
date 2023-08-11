package top.re1ife.vekt.framework.core.common.event;

/**
 * 节点变更事件
 */
public class VektRpcNodeChangeEvent implements VektRpcEvent{

    private Object data;

    public VektRpcNodeChangeEvent(Object data){
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
