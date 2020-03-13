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
 *
 * @Author:lbl
 * @Date:Created in 23:18 2020/3/6
 * @Modified By:
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private void invoke(ChannelHandlerContext ctx, Transport transport) {
        Serializer serializer = SerializerFactory.instance(transport.getSerializeType());
        //解码请求信息
        Request request = (Request) serializer.deserialize(transport.getBytes(), Request.class);
        LOGGER.info("执行函数，id:{}", request.getId());
        //执行方法
        Object response = new ServerInvoker().invoke(request);
        //对响应结果编码
        transport.setBytes(serializer.serialize(response));
        transport.setLength(transport.getBytes().length);
        ctx.channel().writeAndFlush(transport);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            if (msg instanceof Transport) {
                if (ctx.executor().inEventLoop()) {//已经是业务线程
                    LOGGER.info("当前线程处理");

                    invoke(ctx, (Transport) msg);
                } else {
                    LOGGER.info("另开线程，处理");

                    ctx.executor().execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(ctx, (Transport) msg);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("服务端异常" + ctx.channel().isActive() + cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("服务器端-断开连接inactive");
        ctx.fireChannelInactive();
    }
}
