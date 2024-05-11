package com.stt.workflow.wrapper;

import com.stt.workflow.scheduler.CancelableScheduler;
import com.stt.workflow.scheduler.TimerTimeoutScheduler;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 10:56
 * @Version 1.0
 **/
public class CancelableSchedulerFactory {

    /**
     * Timer实现的超时可撤销的任务调度器
     */
    public final static CancelableScheduler cancelablescheduler = new TimerTimeoutScheduler();

    /**
     * 创建可撤销的任务调度器
     * @return
     */
    public static CancelableScheduler cancelablescheduler(){
        return cancelablescheduler;
    }

}
