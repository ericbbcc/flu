package com.flu.web;


import com.flu.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by float.lu on 7/9/16.
 */
public class RandomThreadPoolServlet extends HttpServlet {

    private static Executor executor = Executors.newCachedThreadPool("线程池1");
    private static Executor executor2 = Executors.newCachedThreadPool("线程池2");
    private static Executor executor3 = Executors.newCachedThreadPool("线程池3");


    @Override
    public void init() throws ServletException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(new Random().nextInt(500));
                    } catch (Exception e) {

                    }
                    int num = new Random().nextInt(50);
                    for (int i = 0 ; i < num ; i++) {
                        executor.execute(new Task());
                    }
                    num = new Random().nextInt(50);
                    for (int i = 0 ; i < num ; i++) {
                        executor2.execute(new Task());
                    }
                    num = new Random().nextInt(50);
                    for (int i = 0 ; i < num ; i++) {
                        executor3.execute(new Task());
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static class Task implements Runnable {

        @SuppressWarnings("Since15")
        public void run() {
            LockSupport.parkNanos(Thread.currentThread(), TimeUnit.MILLISECONDS.toNanos(new Random().nextInt(500)));
        }
    }
}
