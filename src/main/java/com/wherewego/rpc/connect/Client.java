package com.wherewego.rpc.connect;

import com.wherewego.rpc.connect.pool.MyChannelPoolHandler;
import com.wherewego.rpc.connect.pool.ServicePool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * 线程不安全，多线程用一个对象要注意
 * @Author:lbl
 * @Date:Created in 19:00 2020/3/7
 * @Modified By:
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;
    private static NioEventLoopGroup group = new NioEventLoopGroup();
    static {
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>(){

            @Override
            protected FixedChannelPool newPool(InetSocketAddress inetSocketAddress) {
                Bootstrap bootstrap=new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY,true);//禁止使用Nagle算法 作用小数据即时传输
                return new FixedChannelPool(bootstrap.remoteAddress(inetSocketAddress),new MyChannelPoolHandler(),2);
            }
        };
    }
    private FixedChannelPool pool;
    private String application;
    private ServicePool.Address address;
    private Channel channel;
    /**
     * 获取连接需要对应的服务名称
     * @param api
     * @return
     */
    public Channel connect(String api){
        if(pool==null){
            reConn(api);
        }
        try {
            channel = pool.acquire().get();
            return channel;
        }catch (ExecutionException|InterruptedException e){
            //对连不上的做标记
            ServicePool.remove(application,address.isd);
            //再连
            reConn(api);
            return connect(api);
        }
    }

    /**
     * 获取新的连接池
     * @param api
     */
    private void reConn(String api){
        LOGGER.info("conn again");
        application = ServicePool.getApplication(api);
        address = ServicePool.getAddress(application);
        if(address==null){
            throw new RuntimeException("连接异常，所有地址均不可用");
        }
        pool = poolMap.get(address.isd);
    }
    public void release(){
        pool.release(channel);
    }
}
