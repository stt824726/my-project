package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 责任链的尾处理器
 * @author: shaott
 * @create: 2024-05-10 15:17
 * @Version 1.0
 **/
@Slf4j
public class EndHandler implements NodeHandler {

    private HandlerChain chain;

    public EndHandler(DefaultHandlerChain chain) {
        this.chain = chain;
    }

    @Override
    public String name() {
        return "end handler";
    }

    @Override
    public String no() {
        return "";
    }

    @Override
    public void handle(Context context) {
        if (chain.getRunnableState() == RunnableState.WAIT_RUN || chain.getRunnableState() == RunnableState.RUNNING) {
            chain.complete(RunnableState.SUCCESS, context);
        } else {
            chain.complete(chain.getRunnableState(), context);
        }
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void stop() {
        log.info("chain stop, No:{}", this.chain.no());
        this.chain.complete(chain.getRunnableState(), null);
    }

    @Override
    public void complete(RunnableState runnablestate, Object data) {
        log.info("chain complete, No:{}", this.chain.no());
        if (chain.getRunnableState() == RunnableState.WAIT_RUN || chain.getRunnableState() == RunnableState.RUNNING) {
            chain.complete(RunnableState.SUCCESS, data);
        } else {
            chain.complete(chain.getRunnableState(), data);
        }
    }

    @Override
    public void setNextHandler(NodeHandler stageHandler){

    }

}
