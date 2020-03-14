package com.wherewego.rpc.invoke;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.connect.Client;
import com.wherewego.rpc.transport.FrameType;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.SerializerType;
import com.wherewego.rpc.transport.Transport;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * 发起请求
 * @Author:lbl
 * @Date:Created in 23:13 2020/3/6
 * @Modified By:
 */
public class RequestInvoker implements Invoker<Request>{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestInvoker.class);

    private boolean async;
    private Invoker invoker;
    private Client client;
    public RequestInvoker(boolean async){
        this.async = async;
        client = new Client();
        this.invoker=new ResponseInvoker(client, async);
    }
    @Override
    public Object invoke(Request request){

        //获取连接
        Channel channel = client.connect(request.getInterfaceName()+request.getBeanName());
        Transport transport = new Transport();
        //这个系列化类型怎么读配置文件呢？
        transport.setFrameType(FrameType.TEXT);
        transport.setSerializeType((SerializerType.PROTO_STUFF));
        Serializer serializer = SerializerFactory.instance(SerializerType.PROTO_STUFF);
        transport.setBytes(serializer.serialize(request));
        transport.setLength(transport.getBytes().length);
        channel.attr(AttributeKey.newInstance("callback")).set(invoker);
        channel.writeAndFlush(transport);
        if(!async){//同步调用，需要加锁等待返回结果
            synchronized (invoker){
                try {
                    invoker.wait(5000);//超时时间 5秒
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return ((ResponseInvoker)invoker).getResult();
        }
        return null;
    }
}
