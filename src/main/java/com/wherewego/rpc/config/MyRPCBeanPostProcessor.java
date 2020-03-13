package com.wherewego.rpc.config;

import com.wherewego.rpc.config.annotation.MyRPCReference;
import com.wherewego.rpc.reflect.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Author:lbl
 * @Date:Created in 18:25 2020/3/7
 * @Modified By:
 */
@Component
public class MyRPCBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        Class<?> clz;
        if (AopUtils.isAopProxy(bean)) {
            clz = AopUtils.getTargetClass(bean);
        } else {
            clz = bean.getClass();
        }
        try {
            for (Field field : clz.getDeclaredFields()) {
                //找到有com.wherewego.rpc.annotation.Reference注解的变量
                MyRPCReference reference = field.getAnnotation(MyRPCReference.class);
                if (reference != null) {
                    boolean access = field.isAccessible();
                    field.setAccessible(true);
                    if (field.get(bean) == null) {
                        //使用动态代理创建bean
                        field.set(bean, ProxyFactory.getProxy(field.getType(), reference.name(), reference.async()));
                    }
                    field.setAccessible(access);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        return bean;
    }
}

