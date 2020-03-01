package com.wherewego.rpc;

import com.wherewego.rpc.config.annotation.Reference;
import com.wherewego.rpc.test.ITest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpcApplicationTests {

    @Reference
    private ITest test;
    @Test
    void contextLoads() {
        System.out.println("test"+test.test("hello world"));
//        if(test!=null){
//            test.test();
//            System.out.println(test.test());
//        }
    }

}
