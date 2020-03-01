package com.wherewego.rpc.handler;

import com.wherewego.rpc.call.Call;
import com.wherewego.rpc.call.NULL;
import com.wherewego.rpc.context.BeanFactory;
import com.wherewego.rpc.test.ITest;
import com.wherewego.rpc.test.TestImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @Author:lbl
 * @Date:Created in 14:00 2020/2/29
 * @Modified By:
 */
@Component
public class Execute {
    @Autowired
    private BeanFactory beanFactory;

    protected Call invoke(Call call){
        if(!check(call)){
            return call;
        }
        String interfaceName = call.getInterfaceName();
        String beanName = call.getBeanName();
        String methodName = call.getMethodName();
        Class<?>[] paramTypes = call.getParamTypes();
        Object[] params = call.getParams();
        try {
            Object bean;
            if(StringUtils.isEmpty(beanName)){
                bean = beanFactory.getApplicationContext().getBean(Class.forName(interfaceName));
            }else{
                bean = beanFactory.getApplicationContext().getBean(beanName);
            }
            if(bean==null){
                call.setResult(new RuntimeException("未找到名称为"+beanName+"的bean"));
                return call;
            }
            Method method = bean.getClass().getMethod(methodName,paramTypes);
            call.setResult(method.invoke(bean,params));
        }catch (ClassNotFoundException e){
            call.setResult(new RuntimeException("未找到接口"+interfaceName+"请填写注解value或在服务端实现接口"));
        }catch (NoSuchBeanDefinitionException e){
            call.setResult(new RuntimeException("未找到名称为"+beanName+"的bean"));
        }catch (NoSuchMethodException e){
            e.printStackTrace();
            call.setResult(new RuntimeException("bean中没有"+methodName+"方法"));
        }catch (Exception e){
            e.printStackTrace();
            call.setResult(new RuntimeException("执行失败"));
        }
        if(call.getResult()==null){
            call.setResult(NULL.nul);
        }
        return call;
    }
    private boolean check(Call call){
        if(call==null|| StringUtils.isEmpty(call.getMethodName())){
            call.setResult(new RuntimeException("call参数不完整"));
            return false;
        }
        if(StringUtils.isEmpty(call.getBeanName())&&StringUtils.isEmpty(call.getInterfaceName())){
            call.setResult(new RuntimeException("接口和bean名称不能同时为空"));
            return false;
        }
        return true;
    }
}
