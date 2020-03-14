package com.wherewego.rpc.handler;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.invoke.Invoker;
import com.wherewego.rpc.transport.FrameType;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.Response;
import com.wherewego.rpc.transport.Transport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 处理客户端连接事件
 * @Author:lbl
 * @Date:Created in 23:17 2020/3/6
 * @Modified By:
 */
public class ClientHandler  extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        LOGGER.info("客户端接收返回值");
        if(msg instanceof Transport){
            Serializer serializer = SerializerFactory.instance(((Transport) msg).getSerializeType());
            //解码请求信息
            Object request = serializer.deserialize(((Transport) msg).getBytes(), Response.class);
            //取出回调方法
            getResponse(ctx,request);
        }

    }
    private void getResponse(ChannelHandlerContext ctx,Object object) throws Exception {
        AttributeKey<Invoker> key = AttributeKey.valueOf("callback");
        Invoker invoker = ctx.channel().attr(key).get();
        if(invoker!=null){
            invoker.invoke(object);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("客户端异常");
        ctx.fireExceptionCaught(cause);
        getResponse(ctx,cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端断开连接inactive");
        ctx.fireChannelInactive();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                LOGGER.info("客户端-写空闲检测");
                //空闲了就发心跳包
                Transport transport = new Transport();
                transport.setFrameType(FrameType.IDLE_NOT_ANSWER);
                ctx.writeAndFlush(transport);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
