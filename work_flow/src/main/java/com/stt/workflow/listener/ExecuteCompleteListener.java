package com.stt.workflow.listener;

import com.stt.workflow.context.Context;

/**
 * @description: 执行完成监听器
 * @author: shaott
 * @create: 2024-05-09 11:25
 * @Version 1.0
 **/
public interface ExecuteCompleteListener<T> {

    /**
     * 责任链执行成功时调用该方法
     * @param context
     * @param data
     */
    void onExecuteSuccess(Context context, T data);

    /**
     * 责任链执行失败时调用该方法
     * @param context
     */
    void onExecuteFail(Context context,T data);

    /**
     * 强制停止责任链的执行时调用该方法
     * @param context
     * @param data
     */
    void onStop(Context context,T data);

    /**
     * 跳过执行时调用该方法
     * @param context
     * @param data
     */
    default void onSkip(Context context,T data){};
}
