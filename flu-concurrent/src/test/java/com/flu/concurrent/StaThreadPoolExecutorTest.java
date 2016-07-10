package com.flu.concurrent;

import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by float.lu on 7/9/16.
 */
public class StaThreadPoolExecutorTest {

    @Test
    public void testInit() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool("test");
        System.out.println(executor.getThreadFactory().getClass().getName());
    }
}
