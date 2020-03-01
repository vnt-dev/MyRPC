package com.wherewego.rpc;

import com.wherewego.rpc.config.annotation.Reference;
import com.wherewego.rpc.test.ITest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpcApplicationTests {

    //客户端使用此注解创建实例，将调用服务端对应实现
    @Reference
    private ITest test;
    @Test
    void contextLoads() {
        //查找服务器端对应方法并执行
        System.out.println("test"+test.test("hello world"));
       // System.out.println("test"+test.toString());toString方法也需要实现，不能直接调用
//        if(test!=null){
//            test.test();
//            System.out.println(test.test());
//        }
    }

}
