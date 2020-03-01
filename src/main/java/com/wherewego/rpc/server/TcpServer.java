package com.wherewego.rpc.server;

import com.wherewego.rpc.codec.CallCodec;
import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.handler.TcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author:lbl
 * @Date:Created in 10:07 2020/2/29
 * @Modified By:
 */
@Service
public class TcpServer {
    @Autowired
    private RpcConfig config;
    @Autowired
    private TcpServerHandler handler;
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    @PostConstruct
    public void init(){
        if(config==null||config.getServerPort()==null||config.getServerPort()<=0){//未配置则不启动
            return;
        }
        try {
            run();
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(),e);
        }
    }
    private void run() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(config.getServerPort())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 自定义长度解码器解决TCP黏包问题
                        // maxFrameLength 最大包字节大小，超出报异常
                        // lengthFieldOffset 长度字段的偏差
                        // lengthFieldLength 长度字段占的字节数
                        // lengthAdjustment 添加到长度字段的补偿值
                        // initialBytesToStrip 从解码帧中第一次去除的字节数
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                        // LengthFieldPrepender编码器，它可以计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
                        pipeline.addLast(new LengthFieldPrepender(4));
                        // 序列化工具
                        pipeline.addLast(new CallCodec());
                        pipeline.addLast(handler);
                    }
                });
        bootstrap.bind().sync();
        LOGGER.info("=========Netty TCP监听已启动，端口{}========",config.getServerPort());
    }
    @PreDestroy
    private void destory() throws InterruptedException {
        if (null != bossGroup) {
            bossGroup.shutdownGracefully().sync();
        }
        if (null != workerGroup) {
            workerGroup.shutdownGracefully().sync();
        }
    }
}
