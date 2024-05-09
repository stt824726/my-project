package com.stt.workflow.handler;

import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 异常处理监听器
 * @author: shaott
 * @create: 2024-05-09 14:01
 * @Version 1.0
 **/
@Slf4j
public class DefaultExceptionListener implements ExceptionListener{
    @Override
    public <T> void onExecutedException(Exception e, T data, Context context) {
        if (context != null) {
            log.error("{}，异常错误的上下文:责任链编号:{}，task编号:{}，组件编号:{}",
                    e.getMessage(), context.getChain().no(),
                    context.getParent() != null ? context.getParent().no() : "",
                    context.getCurrent() != null ? context.getCurrent().no() : "", e);
        }else{
            log.error(e.getMessage(),e);
        }
    }

}
