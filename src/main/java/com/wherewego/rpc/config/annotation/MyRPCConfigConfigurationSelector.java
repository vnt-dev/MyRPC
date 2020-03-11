package com.wherewego.rpc.config.annotation;

import com.wherewego.rpc.config.MyConfig;
import com.wherewego.rpc.context.SpringBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注入配置类
 * @Author:lbl
 * @Date:Created in 21:55 2020/3/9
 * @Modified By:
 */
public class MyRPCConfigConfigurationSelector implements ImportSelector, BeanFactoryAware {
    private BeanFactory beanFactory;
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{MyConfig.class.getName()};
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringBeanFactory.beanFactory = beanFactory;
    }
}
