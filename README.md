# MyRPC
一个RPC框架
## 使用方式：

1.编译代码 mvn install到本地maven仓库

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
#配置服务提供者的端口号（如果当前的工程不是服务提供者，则可以不配置）
wherewego.rpc.protocol.port=8088
#通信协议
wherewego.rpc.protocol.name=wqRpc
#应用名称
wherewego.rpc.application.name=wqRpc
```
4.服务提供者编写接口

在启动类上加入注解，加入注解MyRPC才能启动
```
//包路径为com.wherewego.rpc.config.annotation
@EnableMyRPC
```

测试代码，例：
```
public interface ITest{
    int add(int a,int b);
}
//使用springboot中的注解即可
@Service
public class TestImpl implements ITest{
    @Override
    public int add(int a,int b){
        return a+b;
    }
}
```
5.消费者调用服务端接口

在启动类上加入注解，加入注解MyRPC才能启动
```
//包路径为com.wherewego.rpc.config.annotation
@EnableMyRPC
```

测试代码，例：
```
//使用myrpc提供的注解 在com.wherewego.rpc.config.annotation包下
@Reference
private ITest test;
public void test(){
    int sum = test.add(1,2);
    System.out.println(sum);
}
```
