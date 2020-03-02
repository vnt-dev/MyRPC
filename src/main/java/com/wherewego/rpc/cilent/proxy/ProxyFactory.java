package com.wherewego.rpc.cilent.proxy;

import com.wherewego.rpc.call.Call;
import com.wherewego.rpc.call.NULL;
import com.wherewego.rpc.channelPool.NettyChannelPool;
import com.wherewego.rpc.cilent.ClientConnect;
import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.handler.CallbackHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理类
 *
 * @Author:lbl
 * @Date:Created in 12:23 2020/2/29
 * @Modified By:
 */
@Component
public class ProxyFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyFactory.class);
    @Autowired
    private RpcConfig rpcConfig;
    @Autowired
    private ClientConnect client;
    @Autowired
    private NettyChannelPool nettyChannelPool;

    public <T> T getProxy(Class<T> tClass, final String beanName) {

        final String interfaceName = tClass.getName();
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LOGGER.info("开始调用");
                long t1 = System.currentTimeMillis();
                final Call callResult = new Call();
                // if("wqRpc".equals(rpcConfig.getProtocol())){//tcp协议
//                Channel channel = client.channel("xxx", new CallbackHandler<Call>() {
//                    @Override
//                    public void callback(Call call) {
//                        LOGGER.info("回调函数执行："+call.getResult());
//                        if(!callResult.isBack()){
//                            callResult.setBack(true);
//                            callResult.setResult(call.getResult());
//                        }
//                    }
//                });
                //   }
                Channel channel =  nettyChannelPool.syncGetChannel(new CallbackHandler<Call>() {
                    @Override
                    public void callback(Call call) {
                        LOGGER.info("回调函数执行："+call.getResult());
                        if(!callResult.isBack()){
                            callResult.setBack(true);
                            callResult.setResult(call.getResult());
                        }
                    }
                });
                Call call = new Call();
                call.setBack(false);
                call.setBeanName(beanName);
                call.setInterfaceName(interfaceName);
                call.setMethodName(method.getName());
                call.setParamTypes(method.getParameterTypes());
                call.setParams(args);
                call.setServerName(rpcConfig.getServerName());
                call.setId(123);
                channel.writeAndFlush(call);
                while (!callResult.isBack()){//等待返回结果
                    Thread.yield();
                }
                //释放连接
//                client.release(channel);
                long t2 = System.currentTimeMillis();
                LOGGER.info("耗时{}ms",t2-t1);
                if(NULL.nul.equals(callResult.getResult())){
                    return null;
                }
                if(callResult.getResult() instanceof Exception){
                    throw new RuntimeException(((Exception) callResult.getResult()).getMessage());
                }
                return callResult.getResult();
            }
        };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{tClass}, handler);
    }


}
