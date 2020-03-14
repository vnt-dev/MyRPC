package com.wherewego.rpc.transport;

/**
 * 序列化类型，0x00~0x0F 15种
 * @Author:lbl
 * @Date:Created in 19:04 2020/3/13
 * @Modified By:
 */
public interface SerializerType {
    /*protostuff序列化方式*/
    byte PROTO_STUFF= 0x01;
    /*json序列化*/
    byte JSON = 0x02;
}
