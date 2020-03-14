package com.wherewego.rpc.connect.pool;

import com.wherewego.rpc.codec.TransportCodec;
import com.wherewego.rpc.handler.ClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class MyChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 4, 0));
        //添加编码器和解码器
        pipeline.addLast(new TransportCodec());
        //添加事件处理器
        pipeline.addLast(new ClientHandler());
    }
}
