package com.wherewego.rpc.handler;

import com.wherewego.rpc.call.Call;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端接收到返回结果
 * @Author:lbl
 * @Date:Created in 22:02 2020/2/29
 * @Modified By:
 */
public class TcpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpClientHandler.class);
    private CallbackHandler callback;
    public TcpClientHandler(CallbackHandler<Call> callback){
        this.callback = callback;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        LOGGER.info("客户端接收返回值");
        if(msg instanceof Call){
            callback.callback(msg);
        }else{
            Call call = new Call();
            call.setResult("错误"+msg.getClass());
            callback.callback(call);
        }

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("客户端异常"+cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端断开连接inactive");
        Call call = new Call();
        call.setResult(new RuntimeException("连接已断开"));
        callback.callback(call);
        ctx.fireChannelInactive();
    }
}
