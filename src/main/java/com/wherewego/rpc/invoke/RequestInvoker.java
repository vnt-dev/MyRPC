package com.wherewego.rpc.invoke;

import com.wherewego.rpc.codec.Serializer;
import com.wherewego.rpc.codec.SerializerFactory;
import com.wherewego.rpc.connect.Client;
import com.wherewego.rpc.connect.pool.CallBackFactory;
import com.wherewego.rpc.transport.Request;
import com.wherewego.rpc.transport.Transport;

/**
 * 发起请求
 * @Author:lbl
 * @Date:Created in 23:13 2020/3/6
 * @Modified By:
 */
public class RequestInvoker implements Invoker<Request>{
    private boolean async=false;
    private Invoker invoker;
    public RequestInvoker(){
    }
    public RequestInvoker(boolean async,Invoker invoker){
        this.async = async;
        this.invoker=invoker;
    }
    @Override
    public Object invoke(Request request) {
        Client client = new Client();
        //怎么区分不同服务？,给服务提供者分组，每组中的单元一致？
        Client.RemoteChannel remoteChannel = client.connect(request.getInterfaceName()+request.getBeanName());
        CallBackFactory.Item item=null;
        item = new CallBackFactory.Item();
        if(!async){//同步调用，需要在这里初始化响应
            item.invoker = new ResponseInvoker();
        }
        item.seq = request.getId();
        item.lock = new Object();
        //建立连接和序列号的关系
        CallBackFactory.put(remoteChannel.channel.id(),item);
        Transport transport = new Transport();
        //这个系列化类型怎么读配置文件呢？
        transport.setSerializeType((byte) 0x00000000);
        Serializer serializer = SerializerFactory.instance((byte) 0x00000000);
        transport.setBytes(serializer.serialize(request));
        transport.setLength(transport.getBytes().length);
        remoteChannel.channel.writeAndFlush(transport);
        if(!async){//同步调用，需要加锁等待返回结果
            synchronized (item.lock){
                try {
                    item.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return ((ResponseInvoker)item.invoker).getResult();
        }
        return null;
    }
}
