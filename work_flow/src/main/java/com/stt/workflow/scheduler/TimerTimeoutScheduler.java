package com.stt.workflow.scheduler;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 10:59
 * @Version 1.0
 **/
@Slf4j
public class TimerTimeoutScheduler implements CancelableScheduler {

    //调度的任务列表
    private final ConcurrentMap<String, TimerTask> scheduledTasks = new ConcurrentHashMap<>();

    //timer调度器
    private final Timer timer = new Timer();

    @Override
    public void schedule(String key, Runnable runnable, long delay, TimeUnit timeunit) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("调度了流水线超时任务，流水线编号:}", key);
                    runnable.run();
                } finally {
                    scheduledTasks.remove(key);
                }
            }
        };
        timer.schedule(timerTask, timeunit.toMillis(delay));
        replaceScheduledTask(key, timerTask);
    }

    @Override
    public void cancel(String key) {
        TimerTask timerTask = scheduledTasks.remove(key);
        if (timerTask != null) {
            log.info("取消流水线超时任务，流水线超时编号:{}", key);
            timerTask.cancel();
        }
    }

    @Override
    public void shutdown() {
        scheduledTasks.clear();
        timer.cancel();
    }

    @Override
    public int size() {
        return scheduledTasks.size();
    }


    /**
     * 替换调度任务 如果同一个key原来已经存储在调度任务，则删除并取消调度，没有则新增
     * @param key
     * @param newTimerTask
     */
    private void replaceScheduledTask(String key, TimerTask newTimerTask) {
        TimerTask oldTimerTask = scheduledTasks.remove(key);
        if (oldTimerTask != null) {
            oldTimerTask.cancel();
        }
        scheduledTasks.put(key, newTimerTask);
    }
}
