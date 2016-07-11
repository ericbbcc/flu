package com.flu.concurrent.stastic;

import com.flu.concurrent.StaThreadPoolExecutor;
import com.flu.concurrent.ThreadInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by float.lu on 7/9/16.
 */
public class DefaultEventPublisher implements EventPublisher, Runnable {

    private Thread thread;

    /**
     * 线程池名 -> 线程池
     */
    private Map<String, StaThreadPoolExecutor> poolName2PoolMap = new ConcurrentHashMap<String, StaThreadPoolExecutor>();

    /**
     * 线程 -> 线程信息
     */
    private Map<Thread, ThreadInfo> thread2ThreadInfoMap = new ConcurrentHashMap<Thread, ThreadInfo>();

    /**
     * 线程 -> 线程池
     */
    private Map<Thread, String> thread2PoolNameMap = new ConcurrentHashMap<Thread, String>();

    public void publishThreadPoolCreatedEvent(StaThreadPoolExecutor executor) {
        if (thread == null) {
            initStaInfoComputeLoop();
        }
        poolName2PoolMap.put(executor.getName(), executor);
    }

    public void publishThreadCreatedEvent(StaThreadPoolExecutor executor, Thread t) {
        thread2ThreadInfoMap.put(t, newThreadInfo(t));
        thread2PoolNameMap.put(t, executor.getName());
    }

    private ThreadInfo newThreadInfo(Thread t) {
        return ThreadInfo.builder().thread(t)
                .build().setCreated();
    }

    public void publishThreadStartedEvent(StaThreadPoolExecutor executor, Thread t) {
        if (thread2ThreadInfoMap.get(t) != null) {
            thread2ThreadInfoMap.get(t).setAlive();
        }
    }

    public void publishThreadEndEvent(StaThreadPoolExecutor executor, Thread t) {
        if (thread2ThreadInfoMap.get(t) != null) {
            thread2ThreadInfoMap.get(t).setDie();
        }
    }

    public void publishThreadRunningTaskBeginEvent(StaThreadPoolExecutor executor, Thread t) {
        if ( thread2ThreadInfoMap.get(t) != null) {
            thread2ThreadInfoMap.get(t).setRunningTask();
        }
    }

    public void publishThreadRunningTaskEndEvent(StaThreadPoolExecutor executor, Thread t) {
        if (thread2ThreadInfoMap.get(t) != null) {
            thread2ThreadInfoMap.get(t).setTaskIdle();
        }
    }

    public void publishThreadPoolTerminatedEvent(StaThreadPoolExecutor executor) {
        poolName2PoolMap.remove(executor.getName());
        removePoolThreads(executor.getName());

    }

    /**
     * 情况线程池的统计信息
     * @param name
     */
    private void removePoolThreads(String name) {
        List<Thread> removeNeeded = new ArrayList<Thread>();
        for (Map.Entry<Thread, String> entry : thread2PoolNameMap.entrySet()) {
            if (entry.getValue().equals(name)) {
                removeNeeded.add(entry.getKey());
            }
        }
        for (Thread thread : removeNeeded) {
            thread2PoolNameMap.remove(thread);
            thread2ThreadInfoMap.remove(thread);
        }
    }


    private void initStaInfoComputeLoop () {
        thread = new Thread(this);
        thread.setName("staInfoComputeLooper");
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("Since15")
    public void run() {
        while (true) {
            long parkTime = TimeUnit.MILLISECONDS.toNanos(StaInfoCenter.mySelf().getConfiguration().getInterval());
            LockSupport.parkNanos(thread, parkTime);
            collectStaInfo();
        }
    }

    private void collectStaInfo() {
        Map<String, Long> poolTotalTaskTimeMap = new HashMap<String, Long>();
        Map<String, Long> poolTotalLifeTimeMap = new HashMap<String, Long>();
        for (Map.Entry<Thread, ThreadInfo> entry : thread2ThreadInfoMap.entrySet()) {
            String poolName = thread2PoolNameMap.get(entry.getKey());
            ThreadInfo threadInfo = entry.getValue();
            long totalTaskTime = threadInfo.getTotalTaskTime();
            long totalLifeTime = System.nanoTime() - threadInfo.getStartTime();
            if (threadInfo.isRunningTask() || !entry.getKey().isAlive()) {
                totalTaskTime += (System.nanoTime() - threadInfo.getTaskTime());
            }
            threadInfo.reset();
            if (poolTotalTaskTimeMap.get(poolName) == null) {
                poolTotalTaskTimeMap.put(poolName, 0L);
                poolTotalLifeTimeMap.put(poolName, 0L);
            }
            poolTotalTaskTimeMap.put(poolName, (poolTotalTaskTimeMap.get(poolName) + totalTaskTime));
            poolTotalLifeTimeMap.put(poolName, (poolTotalLifeTimeMap.get(poolName) + totalLifeTime));
            if (!entry.getKey().isAlive()) {
                thread2ThreadInfoMap.remove(entry.getKey());
                thread2PoolNameMap.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, StaThreadPoolExecutor> entry : poolName2PoolMap.entrySet()) {
            String poolName = entry.getKey();
            if(poolTotalTaskTimeMap.get(poolName) == null ||
                    poolTotalLifeTimeMap.get(poolName) == null) {
                processEmptyPool(entry.getKey());
                continue;
            }
            Double effRate = Double.valueOf(poolTotalTaskTimeMap.get(poolName)) * 100D /
                    Double.valueOf(poolTotalLifeTimeMap.get(poolName));
            Node node = new Node();
            node.setDate(new Date().getTime());
            node.setValue(effRate.intValue());
            StaInfoCenter.mySelf().addEffNode(entry.getKey(), node);
        }
    }

    private void processEmptyPool(String poolName) {
        Node node = new Node();
        node.setDate(new Date().getTime());
        node.setValue(0);
        StaInfoCenter.mySelf().addEffNode(poolName, node);
    }
}
