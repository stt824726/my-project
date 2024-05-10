package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 16:04
 * @Version 1.0
 **/
@Slf4j
public abstract class ProcessorHandler extends AbstractHandler {

    @Override
    public void executeNextHandler(Context context) {
        //handler必须执行，而且任务执行不成功时，停止整个职责连的执行
        if (isRequired() && getRunnableState() == RunnableState.FAIL) {
            log.info("{} handler executed failed.", this.name());
            //其中-个handler失败，则整个责任链都失败
            if (this.getNextHandler() != null) {
                this.getNextHandler().stop();
            }
            return;
        }
        if (this.getNextHandler() != null) {
            log.info("current handler: {}, next handler: {}", this.name(), this.getNextHandler().name());
            this.getNextHandler().handle(context);
        } else {
            log.info("{} is the last handler, there is no next handler", this.name());
        }
    }

    @Override
    public void onCurrentContext(Context context) {

    }
}
