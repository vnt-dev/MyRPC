package com.wherewego.rpc.invoke;

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
    public Object getResult() {
        return result;
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
    public Object invoke(Response response) {
        isResponse=true;
        result=response.getResult();

        return result;
    }

}
