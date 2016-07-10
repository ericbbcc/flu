package com.flu.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by float.lu on 7/8/16.
 */
public class Executors {
    public static ExecutorService newCachedThreadPool(String poolName) {
        if (poolName == null) {
            throw new NullPointerException("poolName must not be null");
        }
        return  new StaThreadPoolExecutor(poolName, 0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }
}
