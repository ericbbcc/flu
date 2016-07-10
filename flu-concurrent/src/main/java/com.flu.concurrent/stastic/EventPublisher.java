package com.flu.concurrent.stastic;

import com.flu.concurrent.StaThreadPoolExecutor;

/**
 * Created by float.lu on 7/9/16.
 */
public interface EventPublisher {
    /**
     * 线程池创建
     * @param executor
     */
    void publishThreadPoolCreatedEvent(StaThreadPoolExecutor executor);

    /**
     * 指定线程池的某个线程被创建
     * @param executor
     * @param t
     */
    void publishThreadCreatedEvent(StaThreadPoolExecutor executor, Thread t);

    /**
     * 指定线程池的线程开始运行
     * @param executor
     * @param t
     */
    void publishThreadStartedEvent(StaThreadPoolExecutor executor, Thread t);

    /**
     * 指定线程池的线程结束运行
     * @param executor
     * @param t
     */
    void publishThreadEndEvent(StaThreadPoolExecutor executor, Thread t);

    /**
     * 指定线程池的某个线程开始执行任务
     * @param executor
     * @param t
     */
    void publishThreadRunningTaskBeginEvent(StaThreadPoolExecutor executor, Thread t);

    /**
     * 指定线程池的某个线程结束执行任务
     * @param executor
     * @param t
     */
    void publishThreadRunningTaskEndEvent(StaThreadPoolExecutor executor, Thread t);

    /**
     * 指定线程池结束
     * @param executor
     */
    void publishThreadPoolTerminatedEvent(StaThreadPoolExecutor executor);
}
