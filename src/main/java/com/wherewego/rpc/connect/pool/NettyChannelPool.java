package com.wherewego.rpc.connect.pool;

import com.wherewego.rpc.codec.TransportCodec;
import com.wherewego.rpc.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;

/**
 * 连接池
 *
 * @Author:lbl
 * @Date:Created in 22:18 2020/3/7
 * @Modified By:
 */
public class NettyChannelPool {
    //连接数组
    private Channel[] channels;
    private Object[] locks;
    private String host;
    private int port;
    private int maxChannelCount;
    //这个是用于数据读写的线程组，所有连接都由这个线程组来调度读写事件
    private static NioEventLoopGroup group = new NioEventLoopGroup();
    private static Bootstrap client;

    static {
        client = new Bootstrap();
        client.group(group);
        client.channel(NioSocketChannel.class);
        //给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //添加编码器和解码器
                pipeline.addLast(new TransportCodec());
                //添加事件处理器
                ch.pipeline().addLast(new ClientHandler());
            }
        });
    }

    public NettyChannelPool() {
        this.maxChannelCount = 2;
        this.channels = new Channel[maxChannelCount];
        this.locks = new Object[maxChannelCount];
        for (int i = 0; i < maxChannelCount; i++) {
            this.locks[i] = new Object();
        }
    }

    public NettyChannelPool(String host, int port, int maxChannelCount) {
        this.host = host;
        this.port = port;
        this.channels = new Channel[maxChannelCount];
        this.locks = new Object[maxChannelCount];
        for (int i = 0; i < maxChannelCount; i++) {
            this.locks[i] = new Object();
        }

    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void Handler(ChannelInitializer channelInitializer) {
        client.handler(channelInitializer);
    }

    public Channel getChannel() throws InterruptedException {
        int index = new Random().nextInt(channels.length);
        if (channels[index] != null && channels[index].isActive()) {

            return channels[index];
        }
        synchronized (locks[index]) {
            if (channels[index] != null && channels[index].isActive()) {
                return channels[index];
            }
            ChannelFuture future = client.connect(host, port).sync();
            channels[index] = future.channel();
        }
        return channels[index];
    }

    public void release(Channel channel) {

    }

    /**
     * 移除一个连接
     *
     * @param channel
     */
    public void remove(Channel channel) {
        int i = 0;
        channel.close();
        for (; i < channels.length; i++) {
            if (channel == channels[i]) {//找到对应的连接
                break;
            }
        }
        //加锁移除
        synchronized (locks[i]) {
            if (channel == channels[i]) {
                channels[i] = null;
            }
        }
    }

}
