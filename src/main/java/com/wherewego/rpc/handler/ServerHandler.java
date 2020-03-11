package com.wherewego.rpc.handler;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.invoke.ServerInvoker;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.Transport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理服务器连接事件
 * @Author:lbl
 * @Date:Created in 23:18 2020/3/6
 * @Modified By:
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private ServerInvoker invoker = new ServerInvoker();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        LOGGER.info("服务端接收{}",msg.getClass());
        long t =System.currentTimeMillis();
        try {
            if(msg instanceof Transport){
                Transport transport = (Transport) msg;
                Serializer serializer = SerializerFactory.instance(transport.getSerializeType());
                //解码请求信息
                Object request = serializer.deserialize(transport.getBytes(),Request.class);
                //执行方法
                Object response = invoker.invoke((Request) request);
                //对响应结果编码
                transport.setBytes(serializer.serialize(response));
                transport.setLength(transport.getBytes().length);
                ctx.channel().writeAndFlush(transport);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LOGGER.info("服务器端方法执行时间{}ms",System.currentTimeMillis()-t);

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("服务端异常"+ctx.channel().isActive()+cause.getMessage());

        ctx.fireExceptionCaught(cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("服务器端-断开连接inactive");
        ctx.fireChannelInactive();
    }
}
