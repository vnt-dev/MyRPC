package com.wherewego.rpc.config.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @program: MyRPC
 * @author: lbl
 * @create: 2020-03-15 15:57
 */
public class MyRPCComponentScanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = this.getPackagesToScan(importingClassMetadata);
        //扫描类
        ClassPathBeanDefinitionScanner scanner =
                new ClassPathBeanDefinitionScanner(registry, false);
        TypeFilter service = new AnnotationTypeFilter(MyRPCService.class);
        scanner.addIncludeFilter(service);
        scanner.scan(packagesToScan.toArray(new String[0]));
    }
    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        //获取MyRPCComponentScan注解里配置的包或类
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(MyRPCComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] value = attributes.getStringArray("value");
        Set<String> packagesToScan = new LinkedHashSet(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        Class[] arr = basePackageClasses;
        int len = basePackageClasses.length;

        for(int i = 0; i < len; ++i) {
            Class<?> basePackageClass = arr[i];
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        //配置的为空则获取当前包路径
        return (Set)(packagesToScan.isEmpty() ? Collections.singleton(ClassUtils.getPackageName(metadata.getClassName())) : packagesToScan);
    }
}
