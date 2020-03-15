package com.wherewego.rpc.config.annotation;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author:lbl
 * @Date:Created in 18:56 2020/3/1
 * @Modified By:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(MyRPCConfigConfigurationSelector.class)
@MyRPCComponentScan
public @interface EnableMyRPC {
    @AliasFor(
            annotation = MyRPCComponentScan.class,
            attribute = "basePackages"
    )
    String[] scanBasePackages() default {};

    @AliasFor(
            annotation = MyRPCComponentScan.class,
            attribute = "basePackageClasses"
    )
    Class<?>[] scanBasePackageClasses() default {};
}
