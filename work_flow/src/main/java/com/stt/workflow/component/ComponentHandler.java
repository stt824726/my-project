package com.stt.workflow.component;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.event.ExecuteStateEvent;
import com.stt.workflow.handler.AbstractHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 组件handler
 * @author: shaott
 * @create: 2024-05-09 18:45
 * @Version 1.0
 **/
@Slf4j
@Data
public class ComponentHandler extends AbstractHandler {

    //组件的ID
    private String componentId;

    private ExecuteStateEvent event;

    //组件调用者
    private ComponentInvoker componentInvoker;

    @Override
    public boolean onHandle(Context context) {
        log.info("开始执行组件: {}", context.getCurrent().no());
        try {
            //调用组件执行组件的业务逻辑
            componentInvoker.invoke(this, context);
            //等待组件运行完成
            await();
            getTimeCounter().end();
            log.info("组件执行完成，组件N0:{}，状态:{}", context.getCurrent().no(), getRunnableState().getDescription());
            log.info("当前组件上下文context:{}", this.getContext().getCurrent().no());
            onExecuteComplete(event != null ? event.getLogs() : "");
            if (getRunnableState() == RunnableState.SUCCESS) {
                return true;
            } else {
                //设置责任链为失败
                getChain().setRunnableState(RunnableState.FAIL);
                return false;
            }
        } catch (Exception e) {
            log.info("组件执行报错，组件no:{}，错误:{}", no(), e.getMessage());
            this.setRunnableState(RunnableState.FAIL);
            this.getTimeCounter().end();
            //设置责任链为失败
            getChain().setRunnableState(RunnableState.FAIL);
            //调用完成率件监听器
            onExecuteComplete(e.getMessage());
            getExceptionListener().onExecutedException(e, null, context);
            return false;
        }
    }

    @Override
    public void stop() {
        if (getRunnableState() == RunnableState.WAIT_RUN) {
            //只有待运行handler才能停止
            if (getTimeCounter().getStartTime() == null) {
                getTimeCounter().start();
            }
            //当前handler没有执行完成，则立即停止执行
            setRunnableState(RunnableState.STOP);
            //开始停止
            HandlerData handlerData = new HandlerData(no(), getRunnableState(), getRunnableState().getDescription());
            handlerData.setTimeCounter(getTimeCounter());
            onStartListener(handlerData);
            componentInvoker.stop(this, getContext());
            //调用停止监听器
            getTimeCounter().end();
            handlerData.setTimeCounter(getTimeCounter());
            onStopListener(handlerData);
        }

        if (getNextHandler() != null) {
            getNextHandler().stop();
        }
    }


    @Override
    public void executeNextHandler(Context context) {
        //handler必须执行，而且任务执行不成功时，停止整个职责连的执行
        if (isRequired() && getRunnableState() == RunnableState.FAIL) {
            log.info("{} handler executed failed.", this.no());
            if (this.getNextHandler() != null) {
                this.getNextHandler().stop();
            }
            return;
        }

        if (this.getNextHandler() != null) {
            log.info("current component: {}, next component: {}", this.name(), this.getNextHandler().name());
            this.getNextHandler().handle(context);
        } else {
            log.info("{} is the last component, there is no next component", this.name());
        }
    }

    @Override
    public void onCurrentContext(Context context) {
        log.info("设置当前组件的上下文，组件N0:{}", no());
        context.getCurrentThreadContext().setCurrent(this);
    }

    /**
     * 执行完成
     * 该方法时有外部调用，可能是另外的线程调用，所以context的Threadcontext无效
     *
     * @param event
     */
    public void executeComplete(ExecuteStateEvent event) {
        this.event = event;
        if (event.getRunnablestate() == RunnableState.RUNNING) {
            log.info("{}链的{}组件正在执行", event.getChainNo(), event.getInstanceNo());
        } else {
            this.setRunnableState(event.getRunnablestate());
            log.info("{} 链的{}组件执行完成，耗时:{}", event.getChainNo(), event.getInstanceNo(), getTimeCounter().time());
        }
    }

    /**
     * 等待组件执行完成
     */
    private void await() {
        long startTime = System.currentTimeMillis();
        while (getRunnableState() == RunnableState.RUNNING) {
            log.debug("等待组件执行完成，组件N0:{}，状态:{}", no(), getRunnableState().getDescription());
            try {
                //等待时间
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            long currentTime = System.currentTimeMillis();
            long dfftime = currentTime - startTime;
            //超时则退出
            if (dfftime > getTimeout()) {
                //超时失败，则停止后面的所有节点与组件的执行
                setRunnableState(RunnableState.FAIL);
                log.info("组件运行超时跳出await，组件NO:{}", no());
                return;
            }
        }
    }


    /**
     * 执行完成监听器
     * @param logs
     */
    private void onExecuteComplete(String logs) {
        HandlerData data = new HandlerData(no(), getRunnableState(), logs);
        data.setTimeCounter(getTimeCounter());
        this.onExecuteListener(data);
    }
}
