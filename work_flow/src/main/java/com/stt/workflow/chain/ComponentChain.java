package com.stt.workflow.chain;

import com.stt.workflow.handler.DefaultHandlerChain;
import com.stt.workflow.scheduler.CancelableScheduler;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 15:50
 * @Version 1.0
 **/
public class ComponentChain extends DefaultHandlerChain {

    public ComponentChain(CancelableScheduler cancelableScheduler, Long timeout){
        super(cancelableScheduler,timeout);
    }
}
