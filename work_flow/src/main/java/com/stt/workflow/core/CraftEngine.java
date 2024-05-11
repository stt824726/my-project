package com.stt.workflow.core;

import com.stt.workflow.handler.DefaultHandlerChain;
import com.stt.workflow.handler.MultiDependEndHandler;
import com.stt.workflow.handler.MultiNextStartHandler;
import com.stt.workflow.scheduler.CancelableScheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 工艺流引擎
 * @author: shaott
 * @create: 2024-05-10 19:47
 * @Version 1.0
 **/
@Data
@Slf4j
public class CraftEngine extends DefaultHandlerChain {

   //开始handler
    public MultiNextStartHandler head = new MultiNextStartHandler();

    //结束handler
    public MultiDependEndHandler tail = new MultiDependEndHandler(this);

    //流水线职责链执行的线程池
    private ThreadPoolExecutor threadPoolExecutor = null;

    public CraftEngine(CancelableScheduler cancelablescheduler, Long timeout, ThreadPoolExecutor threadpoolExecutor) {
        super(cancelablescheduler,timeout);
        this.threadPoolExecutor =threadPoolExecutor;
        this.setHead(head);
        this.head.setThreadPoolExecutor(threadPoolExecutor);
    }

}
