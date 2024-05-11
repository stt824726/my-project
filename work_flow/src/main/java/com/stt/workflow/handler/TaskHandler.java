package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.context.ThreadContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 任务节点handler
 * @author: shaott
 * @create: 2024-05-10 19:53
 * @Version 1.0
 **/
@Slf4j
@Data
public class TaskHandler extends AbstractHandler {

    //依赖该任务的后继任务集台
    Set<TaskHandler> nextTasks = new HashSet<>();
    //该任务依赖的前置任务集台
    Set<TaskHandler> dependTasks = new HashSet<>();
    //该任务的入度,当indegree =0时,该任务才会执行,indegree=dependTasks.size()
    AtomicInteger indegree = new AtomicInteger(0);
    /**
     * 流水线职责链执行的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = null;
    //挂载的组件链
    private HandlerChain componentChain;

    public TaskHandler() {

    }


    /**
     * 添加后缕的任务
     *
     * @param taskHandler
     * @return
     */
    public TaskHandler addNextTask(TaskHandler taskHandler) {
        this.nextTasks.add(taskHandler);
        return this;
    }


    /**
     * 添加依赖的前置任务
     *
     * @param taskHandler
     * @return
     */
    public TaskHandler addDependTask(TaskHandler taskHandler) {
        //添加到依赖的task集合中
        this.dependTasks.add(taskHandler);
        //该任务的入度+1
        indegree.incrementAndGet();
        return this;
    }

    @Override
    public boolean onHandle(Context context) {
        log.info("执行到task:{}", name());
        if (componentChain == null) {
            this.setRunnableState(RunnableState.SUCCESS);
            this.getTimeCounter().end();
            onExecuteComplete(getRunnableState().getDescription());
            return true;
        }
        //执行组件链
        log.info("Task开始调用组件链,task no:{}", no());
        componentChain.start(context);
        log.info("Task组件链执行完成,task no:{},状态:{}", no(), componentChain.getRunnableState());
        this.setRunnableState(componentChain.getRunnableState());
        this.getTimeCounter().end();
        onExecuteComplete(getRunnableState().getDescription());
        if (componentChain.getRunnableState() == RunnableState.SUCCESS) {
            return true;
        } else {
            //设置责任链为失败
            getChain().setRunnableState(RunnableState.FAIL);
            return false;
        }
    }


    @Override
    public void stop() {
        log.debug("stop task,name:{}", name());
        if (getRunnableState() == RunnableState.WAIT_RUN || getRunnableState() == RunnableState.RUNNING) {
            //只有等待运行或者正在运行的task才能运行停止本task的逻辑
            if (getTimeCounter().getStartTime() == null) {
                getTimeCounter().start();
            }
            setRunnableState(RunnableState.STOP);
            //开始停Task
            HandlerData startData = new HandlerData(no(), getRunnableState(), getRunnableState().getDescription());
            startData.setTimeCounter(getTimeCounter());
            onStartListener(startData);
            //停止task中的组件链的执行
            if (componentChain != null) {
                componentChain.stop();
            }
            //如果上下文context不为室，则移除ThreadLoca1
            if (getContext() != null) {
                getContext().removeThreadContext();
            }
            getTimeCounter().end();
            //调用停止监听器
            HandlerData handlerData = new HandlerData(no(), getRunnableState(), getRunnableState().getDescription());
            handlerData.setTimeCounter(getTimeCounter());
            onStopListener(handlerData);
        }
        //停止后继task的执行
        for (TaskHandler next : nextTasks) {
            next.stop();
        }
    }

    /**
     * 执行后续task
     *
     * @param context
     */
    public void executeNextHandler(Context context) {
        //删除当前task的上下文
        context.removeThreadContext();

        //handler必须执行，而且任务执行不成功时，停止整个职责连的执行
        if (isRequired() && getRunnableState() == RunnableState.FAIL) {
            log.debug("{} handler executed failed.", this.no());
            for (TaskHandler next : nextTasks) {
                next.stop();
            }
            return;
        }
        //执行后继所有task
        for (TaskHandler next : nextTasks) {
            log.debug("task name: '{}', next task name: {} indegree: {}", name(), next.name(), next.getIndegree().get());
            //next task入度减1
            next.indegree.decrementAndGet();

            //next task入度不为0，还有依赖的前置task未执行完成，该task不能开始执行
            if (next.indegree.get() > 0) {
                continue;
            }
            //next task不能重复执行
            if (next.getRunnableState() != RunnableState.WAIT_RUN) {
                continue;
            }
            log.debug("Put into the thread pool for execution, task name: {}", next.name());
            //启动线程执行后继task
            threadPoolExecutor.submit(new TaskExecuteThread(next, context));
        }
    }

    @Override
    public void onCurrentContext(Context context) {
        ThreadContext threadcontext = context.getCurrentThreadContext();
        threadcontext.setParent(this);
        context.setCurrentThreadContext(threadcontext);
    }


    /**
     * 执行完成监听器
     */
    private void onExecuteComplete(String logs) {
        HandlerData data = new HandlerData(no(), getRunnableState(), logs);
        data.setTimeCounter(getTimeCounter());
        this.onExecuteListener(data);
    }


    /**
     * 设置拦截器
     * @param interceptor
     */
    public void setHandlerInterceptor(HandlerInterceptor interceptor) {
        if (interceptor != null) {
            this.interceptor = interceptor;
        }
    }

    /**
     * Task执行线程
     */
    class TaskExecuteThread implements Runnable {
        private final TaskHandler task;
        private final Context context;

        public TaskExecuteThread(TaskHandler task, Context context) {
            this.task = task;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                task.handle(context);
            } catch (Exception e) {
                getExceptionListener().onExecutedException(e, null, context);
            }
        }
    }
}

