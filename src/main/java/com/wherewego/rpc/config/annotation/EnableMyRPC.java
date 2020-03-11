package com.wherewego.rpc.config.annotation;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

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
public @interface EnableMyRPC {

}
