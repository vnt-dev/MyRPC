package com.wherewego.rpc.cilent.processor;

import com.wherewego.rpc.cilent.proxy.ProxyFactory;
import com.wherewego.rpc.config.annotation.Reference;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * bean初始化前后执行处理
 * @Author:lbl
 * @Date:Created in 15:06 2020/2/29
 * @Modified By:
 */
@Component
public class MyRPCBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private ProxyFactory proxyFactory;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        Class<?> clz;
        if(AopUtils.isAopProxy(bean)){
            clz = AopUtils.getTargetClass(bean);
        }else{
            clz = bean.getClass();
        }
        try {
            for (Field field:clz.getDeclaredFields()){
                //找到有com.wherewego.rpc.annotation.Reference注解的变量
                Reference reference = field.getAnnotation(Reference.class);
                if(reference!=null){
                    System.out.println("postProcessBeforeInitialization..."+reference.value());
                    boolean access = field.isAccessible();

                    field.setAccessible(true);
                    //使用动态代理创建bean
                    field.set(bean, proxyFactory.getProxy(field.getType(),reference.value()));
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
