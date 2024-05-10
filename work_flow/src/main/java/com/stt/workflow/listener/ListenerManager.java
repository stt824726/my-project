package com.stt.workflow.listener;

import lombok.Data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @description: 监听器管理器
 * 包括流水线引擎的监听器，task节点的监听器，组件的监听器
 * @author: shaott
 * @create: 2024-05-10 09:55
 * @Version 1.0
 **/
@Data
public class ListenerManager {

    //流水线引擎执行完成时的监听器列表，可以注册0到n个监听器
    private Queue<ExecuteCompleteListener> engineCompleteListeners = new ConcurrentLinkedDeque<>();

    //流水线引警启动时的监听器列表，可以注册0到n个监听器
    private Queue<StartupListener> engineStartupListeners = new ConcurrentLinkedDeque<>();

    //task执行完成时的监听器列表，可以注册0到n个监听器
    private Queue<ExecuteCompleteListener> taskCompleteListeners = new ConcurrentLinkedDeque<>();

    //task启动时的监听器列表，可以注册0到n个监听器
    private Queue<StartupListener> taskStartupListeners = new ConcurrentLinkedDeque<>();

    //组件执行完成时的监听器列表，可以注册0到n个监听器
    private Queue<ExecuteCompleteListener> componentCompleteListeners = new ConcurrentLinkedDeque<>();

    //组件启动时的监听器列表，可以注册0到n个监听器
    private Queue<StartupListener> componentStartupListeners = new ConcurrentLinkedDeque<>();

    //异常处理监听器
    private ExceptionListener exceptionListener = new DefaultExceptionListener();

    /**
     * 添加流水线引擎执行完成的监听器
     *
     * @param listener
     */
    public void addEngineCompleteListener(ExecuteCompleteListener listener) {
        this.engineCompleteListeners.add(listener);
    }


    /**
     * 添加流水线引擎执行开始的监听器
     *
     * @param listener
     */
    public void addEngineStartupListener(StartupListener listener) {
        this.engineStartupListeners.add(listener);
    }


    /**
     * 添加task执行完成的监听器
     *
     * @param listener
     */
    public void addTaskCompleteListener(ExecuteCompleteListener listener) {
        this.taskCompleteListeners.add(listener);
    }


    /**
     * 添加task执行开始的监听器
     *
     * @param listener
     */
    public void addTaskStartupListener(StartupListener listener) {
        this.taskStartupListeners.add(listener);
    }


    /**
     * 添加组件执行完成的监听器
     *
     * @param listener
     */
    public void addComponentCompleteListener(ExecuteCompleteListener listener) {
        this.componentCompleteListeners.add(listener);
    }


    /**
     * 添加组件执行开始的监听器
     * @param listener
     */
    public void addComponentStartupListener(StartupListener listener) {
        this.componentStartupListeners.add(listener);
    }
}
