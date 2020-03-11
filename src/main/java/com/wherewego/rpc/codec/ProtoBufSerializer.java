package com.wherewego.rpc.codec;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProtoBuf序列化工具
 * @Author:lbl
 * @Date:Created in 22:38 2020/3/5
 * @Modified By:
 */
public class ProtoBufSerializer<T> implements Serializer<T>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoBufSerializer.class);

    @Override
    public byte[] serialize(T t) {
        LinkedBuffer buffer=null;
        try {
            RuntimeSchema<T> schema = RuntimeSchema.createFrom((Class<T>) t.getClass());
            buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            byte[] bytes = ProtostuffIOUtil.toByteArray(t,schema,buffer);
            return bytes;
        }catch (Exception e){
            LOGGER.warn(e.getMessage(),e);
            throw new RuntimeException("编码错误");
        }finally {
            if (buffer==null){
                buffer.clear();
            }
        }
    }

    @Override
    public  T deserialize(byte[] bytes,Class<T> clazz) {
        try {
            RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
            T t = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes,t,schema);
            return t;
        }catch (Exception e){
            LOGGER.warn(e.getMessage(),e);
            throw new RuntimeException("解码错误");
        }
    }
}
