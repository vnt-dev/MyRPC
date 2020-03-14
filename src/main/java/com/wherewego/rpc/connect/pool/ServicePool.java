package com.wherewego.rpc.connect.pool;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放所有服务提供者
 *
 * @Author:lbl
 * @Date:Created in 22:58 2020/3/7
 * @Modified By:
 */
public class ServicePool {
    public static class Address {
        public InetSocketAddress isd;
        public boolean isActive;//状态
        //public int weight=100;//0~100,值越大调用概率也越大
    }

    //一个接口对应一个应用名称
    private static final Map<String, String> apiMap = new HashMap<>();
    //一个应用名称对应了一系列地址
    private static final Map<String, List<Address>> listMap = new HashMap<>();
    //是否配置注册中心
    private static boolean isRegister = false;
    public static final String CENTER = "center";

    public static void setIsRegister(boolean isRegister) {
        ServicePool.isRegister = isRegister;
    }

    /**
     * 添加一个应用
     *
     * @param api
     * @param application
     */
    public static void addApplication(String api, String application) {
        apiMap.put(api, application);
    }

    public static String getApplication(String api) {
        if (isRegister) {
            return apiMap.get(api);
        } else {//没有注册中心的时候，顶多是集群服务，只会有一个队列,所有服务对应的名称都是center
            return ServicePool.CENTER;
        }
    }

    public static void addAddress(String application, String host, int port) {
        List<Address> list = listMap.get(application);
        if (list == null) {
            synchronized (ServicePool.class) {
                list = listMap.get(application);
                if (list == null) {
                    list = new ArrayList<>();
                    listMap.put(application, list);
                }
            }
        }
        Address address = new Address();
        address.isActive = true;
        address.isd = new InetSocketAddress(host, port);
        list.add(address);
    }

    public static Address getAddress(String application) {
        List<Address> list = listMap.get(application);
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Address> activeList = new ArrayList<>();
        for (Address address : list) {//找出有效的地址
            if (address.isActive) {
                activeList.add(address);
            }
        }
        //没有就返回空
        if (activeList.isEmpty()) {
            return null;
        }
        //只有一个没得选
        if (activeList.size() == 1) {
            return activeList.get(0);
        }
        //随机选一个
        int index = new Random().nextInt(activeList.size());
        return activeList.get(index);
    }

    /**
     * 地址不可用，就标记一下,这里有个隐患，服务不可用之后没办法自动恢复
     *
     * @param application
     * @param isd
     */
    public static void remove(String application, InetSocketAddress isd) {
        List<Address> list = listMap.get(application);
        if (list != null) {
            for (Address address : list) {
                if (isd.equals(address.isd)) {
                    address.isActive = false;
                }
            }
        }

    }
}
