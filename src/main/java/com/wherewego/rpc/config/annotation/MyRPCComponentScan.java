package com.wherewego.rpc.config.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描bean
 * @program: MyRPC
 * @author: lbl
 * @create: 2020-03-15 16:03
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({MyRPCComponentScanRegistrar.class})
public @interface MyRPCComponentScan {
    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
