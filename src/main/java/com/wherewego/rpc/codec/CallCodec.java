package com.wherewego.rpc.codec;

import com.wherewego.rpc.call.Call;
import com.wherewego.rpc.utils.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @Author:lbl
 * @Date:Created in 9:28 2020/3/1
 * @Modified By:
 */
public class CallCodec extends MessageToMessageCodec<ByteBuf, Call> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Call call, List<Object> out) throws Exception {
        byte[] data = ProtostuffUtils.serialize(call);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(data);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Object call = ProtostuffUtils.deserialize(bytes,Call.class);
        out.add(call);
    }
}
