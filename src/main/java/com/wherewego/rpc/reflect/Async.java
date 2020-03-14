package com.wherewego.rpc.reflect;

import com.wherewego.rpc.invoke.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步执行方法，内部使用线程内变量传递数据
 * @Author:lbl
 * @Date:Created in 15:04 2020/3/14
 * @Modified By:
 */
public class Async<T> {
    private static Logger logger = LoggerFactory.getLogger(Async.class);
    private final static ThreadLocal<Async> THREAD_LOCAL = new ThreadLocal();
    private Invoker<T> invoker;
    private T result;
    private boolean isResponse;
    private Async(){
        isResponse=false;
    }
    public static <T> Async<T> getInstance(){
        Async<T> async = THREAD_LOCAL.get();
        if(async==null){
            async=new Async<>();
            THREAD_LOCAL.set(async);
        }
        return async;
    }
    public synchronized void setResult(T result) throws Exception {
        this.result = result;
        this.isResponse=true;
        logger.info("异步调用返回结果{}",result);
        if(invoker!=null){
            this.invoker.invoke(result);
        }
    }
    public synchronized void addListener(Invoker<T> invoker){
        if(isResponse){
            try {
                invoker.invoke(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            this.invoker=invoker;
        }
    }
}
