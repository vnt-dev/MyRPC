package com.wherewego.rpc.transport;

/**
 * 返回信息
 * @Author:lbl
 * @Date:Created in 22:53 2020/3/6
 * @Modified By:
 */
public class Response {
    private int id;
    private Object result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
