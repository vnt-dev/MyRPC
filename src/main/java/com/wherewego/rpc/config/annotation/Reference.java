package com.wherewego.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * rpc客户端使用此注解生成引用，只能在属性上使用
 * @Author:lbl
 * @Date:Created in 15:44 2020/2/29
 * @Modified By:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Reference {
    String value() default "";
}
