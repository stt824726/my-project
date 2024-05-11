package com.stt.workflow.wrapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 13:34
 * @Version 1.0
 **/
public class CraftEngineThreadPoolFactory {

    public final static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50,1000,60, TimeUnit.SECONDS,
            new ArrayBlockingQueue(500), new ThreadPoolExecutor.DiscardPolicy());

    public static ThreadPoolExecutor threadPoolExecutor(){
        return threadPoolExecutor;
    }

}
