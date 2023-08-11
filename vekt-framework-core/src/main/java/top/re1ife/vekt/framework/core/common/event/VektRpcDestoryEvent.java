package top.re1ife.vekt.framework.core.common.event;

/**
 * 销毁事件
 */
public class VektRpcDestoryEvent implements VektRpcEvent{

    private Object data;

    public VektRpcDestoryEvent(Object data) {
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
