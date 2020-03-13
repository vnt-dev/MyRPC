package com.wherewego.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @Author:lbl
 * @Date:Created in 15:43 2020/2/29
 * @Modified By:
 */
@Configuration
@ComponentScan("com.wherewego.rpc")
public class MyConfig {
    @Value("${wherewego.rpc.application.name:wqRpc}")
    private String serverName;
    @Value("${wherewego.rpc.address:null}")
    private String address;
    @Value("${wherewego.rpc.protocol.name:wqRpc}")
    private String protocol;
    @Value("${wherewego.rpc.serialize-type:protostuff}")
    private String serializeType;
    @Value("${wherewego.rpc.protocol.port:-1}")
    private Integer port;
    @Value("${wherewego.rpc.register:null}")
    private String registerUri;

    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig config = new RpcConfig();
        config.setServerName(serverName);
        config.setProtocol(protocol);
        config.setServerPort(port);
        config.setRegisterUri(registerUri);
        config.setRemoteAddress(address);
        return config;
    }
}
