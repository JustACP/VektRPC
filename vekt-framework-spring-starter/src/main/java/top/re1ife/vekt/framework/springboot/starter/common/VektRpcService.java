package top.re1ife.vekt.framework.springboot.starter.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 21:32:50
 * @Copyright：re1ife | blog: re1ife.top
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface VektRpcService {
    int limit() default 0;

    String group() default "";

    String serviceToken() default "";
}
