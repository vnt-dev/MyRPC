package com.wherewego.rpc.protocol;

import com.wherewego.rpc.codec.TransportCodec;
import com.wherewego.rpc.handler.ServerHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *为MyRPC协议初始化解码器
 * @Author:lbl
 * @Date:Created in 14:22 2020/3/7
 * @Modified By:
 */
public class MyRPCProtocol implements Protocol{
    @Override
    public void initChannel(ChannelPipeline pipeline) {
//        pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
        pipeline.addLast(new TransportCodec());
        pipeline.addLast(new ServerHandler());
    }
}
