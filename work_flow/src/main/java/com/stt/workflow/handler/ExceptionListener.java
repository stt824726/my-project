package com.stt.workflow.handler;

import com.stt.workflow.context.Context;

/**
 * @description: 异常监听器
 * @author: shaott
 * @create: 2024-05-09 13:59
 * @Version 1.0
 **/
public interface ExceptionListener {

    /**
     * 异常执行时触发
     * @param e
     * @param data
     * @param context
     * @param <T>
     */
    <T> void onExecutedException(Exception e,T data, Context context);
}
