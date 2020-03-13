package com.wherewego.rpc.connect.pool;

import com.wherewego.rpc.invoke.Invoker;
import io.netty.channel.ChannelId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Author:lbl
 * @Date:Created in 22:07 2020/3/7
 * @Modified By:
 */
public class CallBackFactory {
    public static class Item {
        public Integer seq;//序列号
        public Object lock;//回调方法执行器的锁，同步调用时使用
        public Invoker invoker;//回调方法执行器

        @Override
        public boolean equals(Object o) {
            return seq == o;
        }

        @Override
        public int hashCode() {
            return seq;
        }
    }

    private static class SingletonHolder {
        private static final Map<ChannelId, Map<Integer, Item>> INVOKER_MAP = new ConcurrentHashMap<>();
    }

    public static Item get(ChannelId key, Integer seq) {
        Map<Integer, Item> map = SingletonHolder.INVOKER_MAP.get(key);
        if (map != null) {
            return map.get(seq);
        }
        return null;
    }

    public static Item remove(ChannelId key, Integer seq) {
        Map<Integer, Item> map = SingletonHolder.INVOKER_MAP.get(key);
        if (map != null) {
            return map.remove(seq);
        }
        return null;
    }

    public static Map<Integer, Item> remove(ChannelId key) {
        return SingletonHolder.INVOKER_MAP.remove(key);
    }

    public static void put(ChannelId key, Item item) {
        Map<Integer, Item> map = SingletonHolder.INVOKER_MAP.get(key);
        if (map == null) {
            synchronized (key) {
                map = SingletonHolder.INVOKER_MAP.get(key);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    SingletonHolder.INVOKER_MAP.put(key, map);
                }
            }
        }
        map.put(item.seq, item);
    }
}
