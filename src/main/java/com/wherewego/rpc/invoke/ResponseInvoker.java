package com.wherewego.rpc.invoke;

import com.wherewego.rpc.connect.Client;
import com.wherewego.rpc.reflect.Async;
import com.wherewego.rpc.transport.Response;

/**
 * 请求响应处理器
 * @Author:lbl
 * @Date:Created in 23:15 2020/3/6
 * @Modified By:
 */
public class ResponseInvoker implements Invoker<Response>{
    private volatile Object result;
    private volatile boolean isResponse;
    private Client client;
    private boolean isAsync;
    private Async async;
    public ResponseInvoker(Client client,boolean isAsync) {
        this.client = client;
        this.isAsync=isAsync;
        if(isAsync){
            this.async=Async.getInstance();
        }

    }

    public Object getResult() {
        if(isResponse){//接收到了返回值
            return result;
        }
        throw new RuntimeException("响应超时");
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    @Override
    public Object invoke(Response response) throws Exception {
        isResponse=true;
        result=response.getResult();
        //释放连接,归还给连接池
        client.release();

        if(isAsync){//异步调用，设置结果
            async.setResult(result);
        }else{//同步调用，需要通知
            synchronized (this){
                this.notify();
            }
        }
        return result;
    }

}
