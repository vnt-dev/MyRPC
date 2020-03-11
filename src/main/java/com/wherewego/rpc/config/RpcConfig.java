package com.wherewego.rpc.config;

/**
 * @Author:lbl
 * @Date:Created in 16:50 2020/2/29
 * @Modified By:
 */
public class RpcConfig {
    /*服务名称*/
    private String serverName;
    /*服务地址*/
    private String serverHost;
    /*服务端口*/
    private Integer serverPort;
    /*协议名称*/
    private String protocol;
    /*序列化方式*/
    private String serializeType;
    /*注册中心地址*/
    private String registerUri;
    /*远程服务的地址*/
    private String remoteAddress;

    public String getRegisterUri() {
        return registerUri;
    }

    public void setRegisterUri(String registerUri) {
        this.registerUri = registerUri;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
