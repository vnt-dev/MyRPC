package com.wherewego.rpc.invoke;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.transport.FrameType;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.Transport;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessageInvoker implements Invoker<Transport>{
    private static Logger logger = LoggerFactory.getLogger(ServerMessageInvoker.class);
    private ChannelHandlerContext ctx;
    public ServerMessageInvoker(ChannelHandlerContext ctx){
        this.ctx=ctx;
    }
    @Override
    public Object invoke(Transport transport) throws Exception {
        logger.info(transport.toString());
        switch (transport.getFrameType()){
            case FrameType.TEXT:
                invokeMethod(transport);
                break;
            case FrameType.IDLE_NOT_ANSWER://不需要回应的心跳包
                break;
                case FrameType.IDLE_ANSWER:
                    transport.setFrameType(FrameType.IDLE_NOT_ANSWER);
                    ctx.writeAndFlush(transport);
                    break;
        }
        return null;
    }
    private void invokeMethod(Transport transport){
        Serializer serializer = SerializerFactory.instance(transport.getSerializeType());
        //解码请求信息
        Request request = (Request)serializer.deserialize(transport.getBytes(),Request.class);
        //执行方法
        Object response = new ServerMethodInvoker().invoke( request);
        //对响应结果编码
        transport.setBytes(serializer.serialize(response));
        transport.setLength(transport.getBytes().length);
        ctx.channel().writeAndFlush(transport);
    }
}
