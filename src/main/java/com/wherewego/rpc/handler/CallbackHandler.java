package com.wherewego.rpc.handler;

/**
 * 回调函数
 * @Author:lbl
 * @Date:Created in 22:10 2020/2/29
 * @Modified By:
 */
public interface CallbackHandler<T> {
    void callback(T t);
}
