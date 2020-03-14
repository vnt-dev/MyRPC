package com.wherewego.rpc.handler;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.invoke.ServerMessageInvoker;
import com.wherewego.rpc.invoke.ServerMethodInvoker;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.Transport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理服务器连接事件
 * @Author:lbl
 * @Date:Created in 23:18 2020/3/6
 * @Modified By:
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            if(msg instanceof Transport){
                if(ctx.executor().inEventLoop()){//已经是业务线程
                    LOGGER.info("当前线程处理");
                    new ServerMessageInvoker(ctx).invoke((Transport) msg);
                }else{
                    LOGGER.info("另开线程处理");

                    ctx.executor().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new ServerMessageInvoker(ctx).invoke((Transport) msg);
                            } catch (Exception e) {
                                LOGGER.info("执行处理异常",e);
                            }
                        }
                    });
                }
            }
        }catch (Exception e){
            LOGGER.info("执行处理异常",e);
        }

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
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                LOGGER.info("服务器端-读空闲检测");
                //空闲了就关闭
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
