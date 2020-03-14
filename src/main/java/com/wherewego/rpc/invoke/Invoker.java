package com.wherewego.rpc.invoke;

import java.util.concurrent.ExecutionException;

/**
 * 方法处理器接口
 * @Author:lbl
 * @Date:Created in 23:10 2020/3/6
 * @Modified By:
 */
public interface Invoker<T> {
    Object invoke(T t) throws Exception;
}
