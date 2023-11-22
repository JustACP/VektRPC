package top.re1ife.vekt.framework.core.common;

import lombok.Data;

import java.util.concurrent.Semaphore;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 15:17:44
 * @Copyright：re1ife | blog: re1ife.top
 */
@Data
public class ServerServiceSemaphoreWrapper {

    private Semaphore semaphore;

    private int maxNums;

    public ServerServiceSemaphoreWrapper(int maxNums) {
        this.maxNums = maxNums;
        this.semaphore = new Semaphore(maxNums);
    }


}
