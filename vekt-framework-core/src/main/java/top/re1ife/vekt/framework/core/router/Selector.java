package top.re1ife.vekt.framework.core.router;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author re1ife
 * @description: 选择器
 * @date 2023/08/10 23:05:04
 * @Copyright：re1ife | blog: re1ife.top
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Selector {

    /**
     * 服务命名
     */

    private String providerServiceName;
}
