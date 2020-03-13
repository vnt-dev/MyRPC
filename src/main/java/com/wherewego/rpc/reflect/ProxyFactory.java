package com.wherewego.rpc.reflect;

import com.wherewego.rpc.connect.Init;
import com.wherewego.rpc.connect.pool.IntegerFactory;
import com.wherewego.rpc.invoke.RequestInvoker;
import com.wherewego.rpc.transport.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author:lbl
 * @Date:Created in 19:04 2020/3/7
 * @Modified By:
 */
public class ProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyFactory.class);

    public static <T> T getProxy(Class<T> tClass, final String beanName, boolean async) {

        final String interfaceName = tClass.getName();
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //组装请求参数
                Request request = new Request();
                //给请求分配一个id
                request.setId(IntegerFactory.getSeq());
                request.setBeanName(beanName);
                request.setInterfaceName(interfaceName);
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);
                //调用请求处理器
                Object object = new RequestInvoker().invoke(request);
                if (object instanceof Throwable) {
                    throw (Throwable) object;
                }
                return object;
            }
        };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{tClass}, handler);
    }


}
