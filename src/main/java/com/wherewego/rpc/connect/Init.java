package com.wherewego.rpc.connect;

import com.wherewego.rpc.config.RpcConfig;
import com.wherewego.rpc.connect.pool.NettyChannelPool;
import com.wherewego.rpc.connect.pool.ServicePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @Author:lbl
 * @Date:Created in 14:58 2020/3/8
 * @Modified By:
 */
@Service
public final class Init {
    private static final Logger LOGGER = LoggerFactory.getLogger(Init.class);
    @Autowired
    private RpcConfig config;
    @Autowired
    private Server server;
    @PostConstruct
    public void init(){
        if(!StringUtils.isEmpty(config.getRegisterUri())){//没有注册中心，则要初始化调用地址
            ServicePool.setIsRegister(false);
            String ra = config.getRemoteAddress();
            try {
                String[] arr = ra.split(",");

                for (String adStr:arr){
                    int last = adStr.lastIndexOf(":");
                    String host = adStr.substring(0,last);
                    int port = Integer.parseInt(adStr.substring(last+1));
                    ServicePool.addAddress(ServicePool.CENTER,host,port);
                }
            }catch (Exception e){
                throw new RuntimeException("远程地址错误，正确格式为host:port[,host:port]");
            }
        }
        start();
    }

    /**
     * 启动服务
     */
    private void start(){
        if(config==null||config.getServerPort()==null||config.getServerPort()<=0){//未配置则不启动
            return;
        }
        try {
            server.run(config.getServerPort());;
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(),e);
            throw new RuntimeException("启动服务失败");
        }
    }
}
