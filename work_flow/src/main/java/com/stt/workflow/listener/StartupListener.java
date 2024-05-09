package com.stt.workflow.listener;

/**
 * @description: 启动监听器
 * @author: shaott
 * @create: 2024-05-09 13:48
 * @Version 1.0
 **/
public interface StartupListener<T> {

    /**
     * 启动时，调用
     * @param data
     */
    void onStart(T data);
}
