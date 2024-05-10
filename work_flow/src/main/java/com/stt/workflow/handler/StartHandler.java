package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 责任链的头处理器
 * @author: shaott
 * @create: 2024-05-10 15:16
 * @Version 1.0
 **/
@Slf4j
public class StartHandler implements NodeHandler {

    //下一个处理器
    private NodeHandler nextHandler;

    @Override
    public String name() {
        return "start handler";
    }

    @Override
    public String no() {
        return "";
    }

    @Override
    public void handle(Context request) {
        if (this.nextHandler != null) {
            this.nextHandler.handle(request);
        }
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void stop() {
        if (this.nextHandler != null) {
            this.nextHandler.stop();
        }
    }

    @Override
    public void complete(RunnableState runnablestate, Object data) {
        log.debug("start handler complete.");
    }

    @Override
    public void setNextHandler(NodeHandler handler) {
        this.nextHandler = handler;
    }
}
