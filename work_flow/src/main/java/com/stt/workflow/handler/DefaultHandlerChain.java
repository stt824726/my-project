package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.scheduler.CancelableScheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description: 责任链实现类
 * @author: shaott
 * @create: 2024-05-10 13:48
 * @Version 1.0
 **/
@Data
@Slf4j
public class DefaultHandlerChain extends HandlerChain {

    //开始Handler
    NodeHandler head = new StartHandler();

    //结束Handler
    NodeHandler tail = new EndHandler(this);

    //最后一个Handler，初始化为开始Handler
    transient NodeHandler last = head;

    //可取消的调度任务，调度责任链的超时任务，默认使用TimerTimeoutScheduler
    CancelableScheduler cancelableScheduler;

    public DefaultHandlerChain() {
        this(null, null);
    }

    public DefaultHandlerChain(CancelableScheduler cancelableScheduler, Long timeout) {
        this.cancelableScheduler = cancelableScheduler;
        if (timeout != null) {
            this.timeout = timeout;
        }
        head.setNextHandler(tail);
    }

    @Override
    public HandlerChain append(NodeHandler handler) {
        final NodeHandler tmp = this.last;
        this.last = handler;
        tmp.setNextHandler(handler);
        this.last.setNextHandler(tail);
        return this;
    }

    @Override
    public void start(Context context) {
        this.context = context;
        //超时任务加入调度器
        scheduleTimeoutTask(this.timeout);
        //设置责任链为运行中
        this.runnableState = RunnableState.RUNNING;
        //开始计时
        this.timeCounter.start();
        //运行开始之前的业务逻辑处理
        HandlerData data = new HandlerData(no(), runnableState, runnableState.getDescription());
        data.setTimeCounter(timeCounter);
        this.onStartListener(data);
        //开始执行
        this.head.handle(context);
    }

    @Override
    public boolean onHandle(Context context) {
        log.info("执行到chain no:{}", no);
        //调用start方法执行组件责任链
        start(context);
        return true;
    }

    @Override
    public void executeNextHandler(Context context) {
    }

    @Override
    public void onCurrentContext(Context context) {

    }

    @Override
    public void stop() {
        if (this.runnableState == RunnableState.WAIT_RUN || this.runnableState == RunnableState.RUNNING) {
            //责任链已经运行完毕的hand1er不能停止，只能停止待运行与正在运行的hand1er
            this.cancelTimeoutTask();
            this.runnableState = RunnableState.STOP;
            if (this.timeCounter.getStartTime() == null) {
                this.timeCounter.start();
            }
            HandlerData data = new HandlerData(no(), runnableState, runnableState.getDescription());
            data.setTimeCounter(timeCounter);
            onStartListener(data);
        }
        //停止责任链中的handler
        this.head.stop();
    }

    @Override
    public void complete(RunnableState runnablestate, Object data) {
        log.info("责任链运行完成，编号:{}，状态:{}", no(), runnablestate.getDescription());
        this.runnableState = runnablestate;
        this.timeCounter.end();
//整个责任链执行完成时，取消超时调度任务
        this.cancelTimeoutTask();
        //调用运行完成监听器
        HandlerData handlerData = new HandlerData(no(), runnablestate, runnablestate.getDescription());
        handlerData.setTimeCounter(timeCounter);
        onExecuteListener(handlerData);
    }


    /**
     * 调度超时任务
     *
     * @param timeoutMilliseconds
     */
    protected void scheduleTimeoutTask(long timeoutMilliseconds) {
        if (cancelableScheduler == null) {
            return;
        }
        cancelableScheduler.schedule(no(), new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, timeoutMilliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 取消超时任务
     */
    protected void cancelTimeoutTask() {
        if (cancelableScheduler != null) {
            cancelableScheduler.cancel(no());
        }
    }

}
