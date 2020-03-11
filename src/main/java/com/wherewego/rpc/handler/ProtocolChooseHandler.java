package com.wherewego.rpc.handler;

import com.wherewego.rpc.protocol.MyRPCProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用于服务器判断HTTP协议还是TCP协议
 * @Author:lbl
 * @Date:Created in 23:20 2020/3/6
 * @Modified By:
 */
public class ProtocolChooseHandler extends ByteToMessageDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProtocolChooseHandler.class);
    /** 默认暗号长度为23 */
    private static final int MAX_LENGTH = 23;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        String method = getBufStart(byteBuf);
        ChannelPipeline pipeline = ctx.pipeline();
        switch (method){
            case "GET":
            case "POST":
            case "HEAD":
            case "PUT":
            case "DELETE":
            case "OPTIONS":
            case "TRACE":
            case "CONNECT":
                //是http请求
                LOGGER.info("协议方法{}",method);
                break;
                default:
                    new MyRPCProtocol().initChannel(pipeline);
                //普通tcp连接
        }
        pipeline.remove(this);//后续不需要判断了，把自己移除
    }
    private String getBufStart(ByteBuf in){
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }else if(length < MAX_LENGTH){
            return "not http";
        }

        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        in.resetReaderIndex();
        StringBuffer str = new StringBuffer();
        for(int i=0;i<length;i++){
            if(content[i]==0x00100000){//读到空格就结束
                break;
            }
            str.append((char)content[i]);
        }
        return str.toString();
    }
}
