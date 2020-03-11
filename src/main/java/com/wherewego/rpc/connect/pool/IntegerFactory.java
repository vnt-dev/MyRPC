package com.wherewego.rpc.connect.pool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:lbl
 * @Date:Created in 21:49 2020/3/7
 * @Modified By:
 */
public class IntegerFactory {
    private static class SingletonHolder {
        private static final AtomicInteger INSTANCE = new AtomicInteger(0);
    }

    private IntegerFactory(){}

    public static int getSeq() {
        return SingletonHolder.INSTANCE.incrementAndGet();
    }

}
