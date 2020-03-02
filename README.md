# MyRPC
一个RPC框架
## 使用方式：

1.编译代码到本地maven仓库

2.构建springboot工程，在pom文件中加入依赖
```
    <dependency>
        <groupId>com.wherewego</groupId>
        <artifactId>my-rpc</artifactId>
        <version>0.0.1-alpha</version>
    </dependency>
```
3.添加配置信息
```
#配置目标服务的地址
wherewego.rpc.address=127.0.0.1:8088
#配置服务提供者的端口号
wherewego.rpc.protocol.port=8088
#通信协议
wherewego.rpc.protocol.name=wqRpc
#应用名称
wherewego.rpc.application.name=wqRpc
```
