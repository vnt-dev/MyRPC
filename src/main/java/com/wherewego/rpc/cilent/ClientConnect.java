package com.wherewego.rpc.cilent;

import com.wherewego.rpc.codec.CallCodec;
import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.handler.CallbackHandler;
import com.wherewego.rpc.handler.TcpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *用于客户端连接服务
 * @Author:lbl
 * @Date:Created in 17:18 2020/2/29
 * @Modified By:
 */
@Component
public class ClientConnect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnect.class);
    //这个是用于数据读写的线程组，所有连接都由这个线程组来调度读写事件
    EventLoopGroup group = new NioEventLoopGroup();
    @Autowired
    private RpcConfig config;

    /**
     * 线程不用这个连接了，释放掉（有线程池的话就还给线程池）
     * @param channel
     */
    public void release(Channel channel){
        channel.close();
    }
    /**
     * 获得一个连接，因为有可能不同服务由不同的提供者提供，那连接是不一样的，所有要用服务名称来区分
     * 怎么区分就扯到服务注册发现机制了，由注册中心告诉你哪些服务在哪些地址上
     * 知道了服务名称，就能获取到服务提供者的连接（可能有多个，用负载均衡算法取其中一个）
     * @param serverName 需要获取的服务名称
     * @return
     */
    public Channel channel(String serverName, final CallbackHandler callback){
        Bootstrap client = new Bootstrap();

        client.group(group);
        client.channel(NioSocketChannel.class);
        //给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                pipeline.addLast(new LengthFieldPrepender(4));
                // 序列化工具
                pipeline.addLast(new CallCodec());
                ch.pipeline().addLast(new TcpClientHandler(callback));
            }
        });

        LOGGER.info("客户端建立连接");
        //连接服务器
        try {
            ChannelFuture future = client.connect(config.getRemoteHost(), config.getRemotePort()).sync();

            return future.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
