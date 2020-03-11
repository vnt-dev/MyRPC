package com.wherewego.rpc.connect;

import com.wherewego.rpc.connect.pool.NettyChannelPool;
import com.wherewego.rpc.connect.pool.ServicePool;
import io.netty.channel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:lbl
 * @Date:Created in 19:00 2020/3/7
 * @Modified By:
 */
public class Client {
    public class RemoteChannel{
        public String addressID;
        public Channel channel;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    //不同服务地址用不同的连接池，即ip+端口对应一个连接池
    private static Map<String, NettyChannelPool> poolMap=new ConcurrentHashMap<>();

    /**
     * 获取连接需要对应的服务名称
     * @param serverName
     * @return
     */
    public RemoteChannel connect(String serverName){
        //获取一个地址
        ServicePool.Address address = ServicePool.getAddress(serverName);
        if(address==null){
            throw new RuntimeException("连接异常，所有地址均连接不上");
        }
        //拿连接池
        NettyChannelPool pool = poolMap.get(address.id);
        //线程安全
        if(pool==null){
            synchronized (Client.class){
                pool = poolMap.get(address.id);
                if(pool==null){
                    pool = new NettyChannelPool(address.host,address.port,1);
                    poolMap.put(address.id,pool);
                }
            }
        }
        RemoteChannel remoteChannel = new RemoteChannel();
        remoteChannel.addressID=address.id;
        try {
            remoteChannel.channel=pool.getChannel();
        } catch (InterruptedException e) {
            ServicePool.remove(remoteChannel.addressID);
            //再次获取
            return this.connect(serverName);
        }
        return remoteChannel;
    }
    public void release(Channel channel){

    }

    /**
     * 连接不可用时用这个标记
     * @param remoteChannel
     */
    public void remove(RemoteChannel remoteChannel){
        ServicePool.remove(remoteChannel.addressID);
    }
}
