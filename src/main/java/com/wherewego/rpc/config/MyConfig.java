package com.wherewego.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @Author:lbl
 * @Date:Created in 15:43 2020/2/29
 * @Modified By:
 */
@Configuration
public class MyConfig {
    @Value("${wherewego.rpc.application.name:wqRpc}")
    private String serverName;
    @Value("${wherewego.rpc.address:null}")
    private String address;
    @Value("${wherewego.rpc.protocol.name:wqRpc}")
    private String protocol;
    @Value("${wherewego.rpc.protocol.port:-1}")
    private Integer port;
    @Bean
    public RpcConfig rpcConfig(){
        RpcConfig config = new RpcConfig();
        config.setServerName(serverName);
        config.setProtocol(protocol);
        config.setServerPort(port);
        if(!StringUtils.isEmpty(address)){
            try {
                int last = address.lastIndexOf(":");
                if(last<=0){
                    throw new RuntimeException("远程地址有误");
                }
                config.setRemotePort(Integer.parseInt(address.substring(last+1)));
                config.setRemoteHost(address.substring(0,last));
            }catch (Exception e){
                throw new RuntimeException("远程地址有误");
            }
        }
        return config;
    }
}
