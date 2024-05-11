package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 该处理器有多个前置依赖TaskHandler 依赖从1到多个Handler，只有依赖的所有Handler全部处理完成，才会执行该处理器
 * @author: shaott
 * @create: 2024-05-10 19:49
 * @Version 1.0
 **/
@Slf4j
public class MultiDependEndHandler extends TaskHandler {

    /**
     * 该处理器所在的责任链
     */
    private final HandlerChain chain;

    public MultiDependEndHandler(HandlerChain chain) {
        this.chain = chain;
    }

    @Override
    public String name() {
        return "Multi depend end handler";
    }

    @Override
    public void handle(Context context) {
        log.info("craft engine execute completed, No: {}", this.chain.no());
        if (chain.getRunnableState() == RunnableState.RUNNING) {
            chain.complete(RunnableState.SUCCESS, context);
        } else {
            chain.complete(chain.getRunnableState(), context);
        }
    }

    @Override
    public void stop() {
        log.info("craft engine stop,No:{}", this.chain.no());
        if (chain.getRunnableState() == RunnableState.RUNNING) {
            chain.complete(RunnableState.SUCCESS, null);
        } else {
            chain.complete(chain.getRunnableState(),null);
        }
    }
}
