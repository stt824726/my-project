package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;

/**
 * @description: 节点拦截器接口
 * @author: shaott
 * @create: 2024-05-09 14:28
 * @Version 1.0
 **/
public interface HandlerInterceptor {

    /**
     * 前置处理
     * @param data
     * @return
     */
    default boolean preHandle(Object data){
        return true;
    }


    /**
     * 后置处理
     * @param runnableState
     * @param data
     */
    default void postHandle(RunnableState runnableState, Object data){

    }
}
