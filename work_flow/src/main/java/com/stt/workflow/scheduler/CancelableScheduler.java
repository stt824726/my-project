package com.stt.workflow.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @description: 可以撤销的任务调度接口
 * @author: shaott
 * @create: 2024-05-10 14:53
 * @Version 1.0
 **/
public interface CancelableScheduler {

    /**
     * 添加调度任务
     *
     * @param key
     * @param runnable
     * @param delay
     * @param timeunit
     */
    void schedule(String key, Runnable runnable, long delay, TimeUnit timeunit);

    /**
     * 取消调度任务
     *
     * @param key
     */
    void cancel(String key);

    /**
     * 停止关闭调度器
     */
    void shutdown();

    /**
     * 任务数量
     *
     * @return
     */
    int size();
}
