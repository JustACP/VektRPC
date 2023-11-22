package top.re1ife.vekt.framework.springboot.starter.common;

import java.lang.annotation.*;

/**
 * @author re1ife
 * @description:
 * @date 2023/08/20 21:31:33
 * @Copyright：re1ife | blog: re1ife.top
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VektRpcReference {
    String url() default "";

    String group() default "";

    String serviceToken() default "";

    int timeOut() default 3000;

    int retry() default 1;

    boolean async() default false;
}
