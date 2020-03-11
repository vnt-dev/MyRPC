package com.wherewego.rpc.codec;

import io.netty.buffer.ByteBuf;

/**
 * @Author:lbl
 * @Date:Created in 22:35 2020/3/5
 * @Modified By:
 */
public interface Serializer<T> {
    byte[] serialize(T t);
    T deserialize(byte[] bytes,Class<T> clazz);
}
