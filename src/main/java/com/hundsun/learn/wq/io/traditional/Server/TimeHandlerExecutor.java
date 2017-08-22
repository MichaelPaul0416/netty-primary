package com.hundsun.learn.wq.io.traditional.Server;

import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangqiang20995
 * @description: 线程池
 * @Date:2017/8/6
 */
public class TimeHandlerExecutor {
    private Executor executor;

    private Logger logger = Logger.getLogger(TimeHandlerExecutor.class);

    public  TimeHandlerExecutor(int maxPoolSize,int queueSize){
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
        logger.info("threadPool initialized successfully...");
    }

    public void execute(Runnable task){
        logger.info("begin execute task ["+Thread.currentThread().getName()+"]");
        try {
            executor.execute(task);
            logger.info("execute task ["+Thread.currentThread().getName()+"] successfully");
        }catch (Exception e){
            logger.error("execute task ["+Thread.currentThread().getName()+"] failed");
        }

    }
}
