package com.wherewego.rpc.handler;

import com.wherewego.rpc.call.Call;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务器接收到信息，完成处理
 * @Author:lbl
 * @Date:Created in 10:56 2020/2/29
 * @Modified By:
 */
@Component
@ChannelHandler.Sharable
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerHandler.class);
    @Autowired
    private Execute execute;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        LOGGER.info("服务端接收{}",msg.getClass());
        try {
            if(msg instanceof Call){
                Call call1 = execute.invoke((Call)msg);
                ctx.channel().writeAndFlush(call1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("服务端异常"+ctx.channel().isActive()+cause.getMessage());
        ctx.channel().close();
        ctx.fireExceptionCaught(cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("服务器端-断开连接inactive");
        ctx.fireChannelInactive();
    }


}
