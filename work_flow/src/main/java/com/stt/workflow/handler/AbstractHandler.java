package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.listener.DefaultExceptionListener;
import com.stt.workflow.listener.ExceptionListener;
import com.stt.workflow.listener.ExecuteCompleteListener;
import com.stt.workflow.listener.StartupListener;
import com.stt.workflow.timer.TimeCounter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @description: 调用流程处理器抽象类
 * @author: shaott
 * @create: 2024-05-09 10:26
 * @Version 1.0
 **/
@Slf4j
@Data
public abstract class AbstractHandler implements NodeHandler {

    //时间计时器，记录handler的执行时间，包括开始、结束时间
    final TimeCounter timeCounter = new TimeCounter();

    //handler执行完成后的监听器列表，可以注册0到n个监听器
    final Queue<ExecuteCompleteListener> completeListeners = new ConcurrentLinkedDeque<>();

    //handler启动时的监听器列表，可以注册0到n个监听器
    final Queue<StartupListener> startupListeners = new ConcurrentLinkedDeque<>();

    //节点开关，是否运行该节点，true:运行，false:不运行，默认运行
    private boolean onOff = true;

    //当前节点必须完成才能执行下一节点
    private final boolean isRequired = true;

    //编号
    String no;

    //名称
    String name;

    //handler处理超时时间，单位:毫秒，默认30分钟
    Long timeout = 30 * 60 * 1000L;

    //当前handler的运行状态，创建handler时默认”待运行
    RunnableState runnableState = RunnableState.WAIT_RUN;

    //handler的拦截器
    HandlerInterceptor interceptor = new HandlerInterceptor() {
    };

    //异常处理监听器
    ExceptionListener exceptionListener = new DefaultExceptionListener();

    //上下文
    Context context;

    //责任链对象
    HandlerChain chain;

    //下一个处理器
    private NodeHandler nextHandler;

    public AbstractHandler() {
    }

    //handler实现的具体方法
    public abstract boolean onHandle(Context context);

    //执行下一个handler
    public abstract void executeNextHandler(Context context);

    //处理当前线程的上下文
    public abstract void onCurrentContext(Context context);

    @Override
    public void handle(Context context) {
        log.info("开始执行节点，no:{}", no);
        this.context = context;
        this.timeCounter.start();
        this.runnableState = RunnableState.RUNNING;
        this.onCurrentContext(context);
        HandlerData startData = new HandlerData(no(), runnableState, runnableState.getDescription());
        startData.setTimeCounter(timeCounter);
        this.onStartListener(startData);
        if (onOff) {
            //进行前置处理
            boolean result = onHandle(context);
            //handler处理
            if (result) {
                this.runnableState = RunnableState.SUCCESS;
            } else {
                this.runnableState = RunnableState.FAIL;
            }
            this.timeCounter.end();
        } else {
            this.runnableState = RunnableState.SKIP;
            this.timeCounter.end();
            HandlerData data = new HandlerData(no(), runnableState, runnableState.getDescription());
            data.setTimeCounter(timeCounter);
            onSkipListener(data);
        }
        //执行后继handler
        executeNextHandler(context);
    }

    @Override
    public void stop() {
        if (runnableState == RunnableState.WAIT_RUN) {
            //只有待运行handler才能停止
            this.timeCounter.end();
            //当前handler没有执行完成，则立即停止执行
            runnableState = RunnableState.STOP;
            HandlerData handlerData = new HandlerData(no(), runnableState, runnableState.getDescription());
            handlerData.setTimeCounter(timeCounter);
            onStopListener(handlerData);
            if (this.nextHandler != null) {
                this.nextHandler.stop();
            }
        }
    }


    @Override
    public void complete(RunnableState runnableState, Object data) {
        log.info("节点运行完成，编号:", runnableState.getDescription());
        this.runnableState = runnableState;
        this.timeCounter.end();
        //执行完成后调用监听器
        HandlerData handlerData = new HandlerData(no(),runnableState, runnableState.getDescription());
        handlerData.setTimeCounter(getTimeCounter());
        this.onExecuteListener(handlerData);
    }

    /**
     * 添加完成监听器
     *
     * @param listener
     * @param <T>
     */
    public <T> void addCompleteListener(ExecuteCompleteListener<T> listener) {
        this.completeListeners.add(listener);
    }

    /**
     * 添加启动时监听器
     *
     * @param listener
     * @param <T>
     */
    public <T> void addStartupListener(StartupListener listener) {
        this.startupListeners.add(listener);
    }


    /**
     * 处理执行成功后的业务逻辑，执行成功调用该方法
     *
     * @param data
     */
    public void onExecuteSuccessListener(Object data) {
        try {
            for (ExecuteCompleteListener listener : completeListeners) {
                listener.onExecuteSuccess(this.context, data);
            }
        }catch(Exception e){
            exceptionListener.onExecutedException(e, data, this.context);
        }
    }

    /**
     * 处理执行失败后的业务逻辑，执行失败调用该方法
     *
     * @param data
     */
    public void onExecuteFailListener(Object data) {
        try {
            for (ExecuteCompleteListener listener : completeListeners) {
                listener.onExecuteFail(this.context, data);
            }
        } catch (Exception e) {
            exceptionListener.onExecutedException(e, data, this.context);
        }
    }

    /**
     * 处理被强制处理时要处理的业务逻辑，被强制停止时调用该方法
     */
    public void onStopListener(Object data) {
        try {
            for (ExecuteCompleteListener listener : completeListeners) {
                listener.onStop(this.context, data);
            }
        } catch (Exception e) {
            exceptionListener.onExecutedException(e, data, this.context);
        }
    }

    /**
     * 处理跳过后的业务逻辑
     *
     * @param data
     */
    public void onSkipListener(Object data) {
        try {
            for (ExecuteCompleteListener listener : completeListeners) {
                listener.onSkip(this.context, data);
            }
        } catch (Exception e) {
            exceptionListener.onExecutedException(e, data, this.context);
        }
    }

    /**
     * 处理执行开始之前的业务逻辑
     *
     * @param data
     */
    public void onStartListener(Object data) {
        try {
            for (StartupListener listener : startupListeners) {
                listener.onStart(data);
            }
        } catch (Exception e) {
            exceptionListener.onExecutedException(e, data, this.context);
        }
    }

    /**
     * 处理执行后的业务逻辑，包括成功、失败、停止
     *
     * @param data
     */
    public void onExecuteListener(Object data) {
        if (runnableState == RunnableState.SUCCESS) {
            this.onExecuteSuccessListener(data);
        } else if (runnableState == RunnableState.FAIL) {
            this.onExecuteFailListener(data);
        } else if (runnableState == RunnableState.STOP) {
            this.onStopListener(data);
        }
    }

    @Override
    public String name(){
        return this.name;
    }

    @Override
    public String no(){
        return this.no;
    }
}
