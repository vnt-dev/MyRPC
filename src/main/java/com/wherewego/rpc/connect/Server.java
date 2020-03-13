package com.wherewego.rpc.connect;

import com.wherewego.rpc.codec.TransportCodec;
import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.handler.ProtocolChooseHandler;
import com.wherewego.rpc.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author:lbl
 * @Date:Created in 16:10 2020/3/7
 * @Modified By:
 */
@Service
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void run(int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("socketChoose", new ProtocolChooseHandler());
//                        pipeline.addLast(new TransportCodec());
//                        pipeline.addLast(new ServerHandler());
                    }
                });
        bootstrap.bind().sync();
        LOGGER.info("=========Netty TCP监听已启动，端口{}========", port);
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
