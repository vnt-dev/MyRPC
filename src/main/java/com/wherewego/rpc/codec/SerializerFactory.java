package com.wherewego.rpc.codec;

import com.wherewego.rpc.config.RpcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author:lbl
 * @Date:Created in 22:46 2020/3/5
 * @Modified By:
 */
public class SerializerFactory  {
    public static Serializer instance(byte type){
        switch (type){
            case 0x00000000://protostuff
                return new ProtoBufSerializer<>();
            default:
                return null;
        }
    }
}
