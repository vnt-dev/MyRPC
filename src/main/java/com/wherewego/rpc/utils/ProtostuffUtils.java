package com.wherewego.rpc.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.wherewego.rpc.call.Call;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author:lbl
 * @Date:Created in 11:24 2020/2/29
 * @Modified By:
 */
public class ProtostuffUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtostuffUtils.class);
    public static <T> byte[] serialize(T source){
        LinkedBuffer buffer=null;
        try {
            RuntimeSchema<T> schema = RuntimeSchema.createFrom((Class<T>) source.getClass());
            buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            return ProtostuffIOUtil.toByteArray(source,schema,buffer);
        }catch (Exception e){
            LOGGER.warn(e.getMessage(),e);
        }finally {
            if (buffer==null){
                buffer.clear();
            }
        }
        return null;
    }
    public static <T> ByteBuf serializeToBuf(T source){
        byte[] bytes = serialize(source);
        return Unpooled.copiedBuffer(bytes);
    }
    public static <T> T deserialize(byte[] data,Class<T> clazz){
        try {
            RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
            T t = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(data,t,schema);
            return t;
        }catch (Exception e){
            LOGGER.warn(e.getMessage(),e);
        }
        return null;
    }
    public static <T> T deserialize(ByteBuf byteBuf, Class<T> clazz){
        LOGGER.info("==========="+byteBuf.readableBytes());
        byte[] result = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(result);
        T call = ProtostuffUtils.deserialize(result,clazz);
        return call;
    }
}
