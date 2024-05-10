package com.stt.workflow.wrapper;

import com.stt.workflow.handler.HandlerInterceptor;
import com.stt.workflow.listener.ExceptionListener;
import com.stt.workflow.listener.ExecuteCompleteListener;
import com.stt.workflow.listener.StartupListener;
import lombok.Data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @description: 包装类Wrapper
 * 抽象类，封装节点的信息，并构建Handler
 * @author: shaott
 * @create: 2024-05-10 11:15
 * @Version 1.0
 **/
@Data
public abstract class Wrapper<T> {

    //节点唯一编号
    private String no;

    //该wrapper关联节点的名称
    private String name;

    //下一个节点的包装类
    private Wrapper<T> nextWrapper;

    //前置与后置处理的拦截器
    private HandlerInterceptor interceptor = new HandlerInterceptor() {
    };

    //节点开关，是否运行该节点，true:运行，false:不运行，默认运行
    private boolean onoff = true;

    //处理超时时间，单位:毫秒
    private Long timeout = 30 * 60 * 1000L;

    //handler执行完成时的监听器列表，可以注册0到n个监听器
    private Queue<ExecuteCompleteListener> completeListeners = new ConcurrentLinkedDeque<>();

    //handler启动时的监听器列表，可以注册0到n个监听器
    private Queue<StartupListener> startupListeners = new ConcurrentLinkedDeque<>();

    //异常处理监听器
    private ExceptionListener exceptionListener;


    /**
     * 设置Handler编号
     *
     * @param no
     * @return
     */
    public Wrapper no(String no) {
        this.no = no;
        return this;
    }


    /**
     * 设置handler名称
     *
     * @param name
     * @return
     */
    public Wrapper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置下一个wrapper
     *
     * @param wrapper
     * @return
     */
    public Wrapper nextWrapper(Wrapper<T> wrapper) {
        this.nextWrapper = wrapper;
        return this;
    }


    /**
     * 设置handler的拦截器
     *
     * @param interceptor
     * @return
     */
    public Wrapper interceptor(HandlerInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }


    /**
     * 设置Handler开关，是否运行该Handler onoff true:运行，false:不运行
     *
     * @param onoff
     * @return
     */
    public Wrapper onoff(boolean onoff) {
        this.onoff = onoff;
        return this;
    }


    /**
     * 设置Handler运行的超时时间
     *
     * @param timeout
     * @return
     */
    public Wrapper timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }


    /**
     * 添加执行完成的监听器
     *
     * @param listener
     * @return
     */
    public Wrapper addExecuteCompleteListener(ExecuteCompleteListener listener) {
        this.completeListeners.add(listener);
        return this;
    }


    /**
     * 添加执行开始的监听器
     *
     * @param listener
     * @return
     */
    public Wrapper addStartupListener(StartupListener listener) {
        this.startupListeners.add(listener);
        return this;
    }

    public String getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public Wrapper<T> getNextwrapper() {
        return nextWrapper;
    }

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }

    public boolean isOnoff() {
        return onoff;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Queue<ExecuteCompleteListener> getCompleteListeners() {
        return completeListeners;
    }

    public void setCompleteListeners(Queue<ExecuteCompleteListener> completeListeners) {
        this.completeListeners = completeListeners;
    }

    public Queue<StartupListener> getStartupListeners() {
        return startupListeners;
    }

    public void setStartupListeners(Queue<StartupListener> startupListeners) {
        this.startupListeners = startupListeners;
    }

    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    /**
     * 构建Handler对象，根据wrapper的属性值构建
     */
    public abstract T build();
}
