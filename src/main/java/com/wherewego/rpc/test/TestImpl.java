package com.wherewego.rpc.test;

import org.springframework.stereotype.Service;

/**
 * 服务端测试代码
 * @Author:lbl
 * @Date:Created in 21:59 2020/2/29
 * @Modified By:
 */
@Service
public class TestImpl implements ITest{
    @Override
    public String test(String msg) {
        StringBuffer str = new StringBuffer("收到：");
        str.append(msg);
        str.append("\n");
        String rs = "test调用返回结果.............";
        for (int i=0;i<10;i++){
            str.append(rs);
        }
        return str.toString();
    }
}
