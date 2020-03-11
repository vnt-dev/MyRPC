package com.wherewego.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @Author:lbl
 * @Date:Created in 18:13 2020/3/7
 * @Modified By:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MyRPCService {
    String name() default "";
}
