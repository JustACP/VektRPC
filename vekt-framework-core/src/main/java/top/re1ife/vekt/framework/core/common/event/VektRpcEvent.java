package top.re1ife.vekt.framework.core.common.event;

public interface VektRpcEvent {

    Object getData();

    VektRpcEvent sendData(Object data);

}
