package com.flu.concurrent;

import com.flu.concurrent.stastic.DefaultEventPublisher;
import com.flu.concurrent.stastic.EventPublisher;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 应有统计功能的线程池
 * Created by float.lu on 7/8/16.
 */
public class StaThreadPoolExecutor extends ThreadPoolExecutor implements Nameable {

    private static EventPublisher eventPublisher = new DefaultEventPublisher();

    private String threadPoolName;

    public StaThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.threadPoolName = poolName;
        initialize();
    }

    public StaThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.threadPoolName = poolName;
        initialize();
    }

    public StaThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.threadPoolName = poolName;
        initialize();
    }

    public StaThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadPoolName = poolName;
        initialize();
    }

    private void initialize() {
        eventPublisher.publishThreadPoolCreatedEvent(this);
        setThreadFactory(new DelegateThreadFactory(this, getThreadFactory()));
    }

    public String getName() {
        return threadPoolName;
    }

    public void setName(String name) {
        this.threadPoolName = name;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        eventPublisher.publishThreadRunningTaskBeginEvent(this, t);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        eventPublisher.publishThreadRunningTaskEndEvent(this, Thread.currentThread());
    }

    @Override
    protected void terminated() {
        eventPublisher.publishThreadPoolTerminatedEvent(this);
        super.terminated();
    }


    public class DelegateThreadFactory implements ThreadFactory {
        private StaThreadPoolExecutor executor;
        private ThreadFactory threadFactory;

        public DelegateThreadFactory(StaThreadPoolExecutor executor, ThreadFactory threadFactory) {
            this.executor = executor;
            this.threadFactory = threadFactory;
        }

        public Thread newThread(Runnable r) {
            Thread t = threadFactory.newThread(new DelegateRunnable(executor, r));
            eventPublisher.publishThreadCreatedEvent(executor, t);
            StringBuilder sb = new StringBuilder();
            sb.append((executor.getName() != null ? executor.getName() : ""));
            sb.append("-");
            sb.append(t.getName());
            t.setName(sb.toString());
            return t;
        }
    }

    public class DelegateRunnable implements Runnable {
        private StaThreadPoolExecutor executor;
        private Runnable r;

        public DelegateRunnable(StaThreadPoolExecutor executor, Runnable r) {
            this.executor = executor;
            this.r = r;
        }

        public void run() {
            eventPublisher.publishThreadStartedEvent(executor, Thread.currentThread());
            r.run();
            eventPublisher.publishThreadEndEvent(executor, Thread.currentThread());
        }
    }

}
