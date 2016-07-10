package com.flu.concurrent;

import lombok.Builder;
import lombok.Data;

/**
 * Created by float.lu on 7/9/16.
 */
@Data
@Builder
public class ThreadInfo {

    private Thread thread;

    /**
     * 工作总时间
     */
    private long totalTaskTime = 0;

    /**
     * 用于计算每次工作时间
     */
    private long taskTime = 0;

    /**
     * 总时间
     */
    private long startTime;

    /**
     * 线程状态
     * 1. 线程开始活着
     * 2. 线程正在执行任务
     * 3. 线程没有在执行任务
     * 4. 线程即将结束
     */
    private int state = 1;

    private static int THREAD_CREATED = 1;
    private static int THREAD_ALIVE = 2;
    private static int TASK_RUNNING = 3;
    private static int TASK_IDLE = 4;
    private static int THREAD_DIE = 16;

    public ThreadInfo setCreated() {
        state = THREAD_CREATED;
        return this;
    }

    public ThreadInfo setAlive() {
        state = THREAD_ALIVE;
        startTime = System.nanoTime();
        return this;
    }

    public ThreadInfo setRunningTask() {
        state = TASK_RUNNING;
        taskTime = System.nanoTime();
        return this;
    }

    public ThreadInfo setTaskIdle() {
        state = TASK_IDLE;
        long workTime = System.nanoTime() - taskTime;
        totalTaskTime += workTime;//累加工作时间
        return this;
    }

    public ThreadInfo setDie() {
        state = THREAD_DIE;
        return this;
    }

    public boolean isRunningTask() {
        return state == TASK_RUNNING;
    }

    public void reset() {
        taskTime = System.nanoTime();
        startTime = taskTime;
        totalTaskTime = 0;
    }
}
