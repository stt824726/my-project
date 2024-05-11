package com.stt.workflow.wrapper;

import com.stt.workflow.core.CraftEngine;
import com.stt.workflow.exception.BussinessException;
import com.stt.workflow.handler.TaskHandler;
import com.stt.workflow.scheduler.CancelableScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 流引擎分装类
 * @author: shaott
 * @create: 2024-05-11 09:27
 * @Version 1.0
 **/
public class CraftEngineWrapper extends Wrapper<CraftEngine> {

    /**
     * task列表
     */
    private final Map<String, TaskWrapper> taskWrappers = new HashMap<>();
    /**
     * 可取消的调度任务，调度责任链的超时任务
     */
    private CancelableScheduler cancelablescheduler;
    /**
     * 流水线职责链执行的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    /**
     * 设置可取消调度器cancelablescheduler
     *
     * @param cancelablescheduler
     * @return
     */
    public CraftEngineWrapper cancelableScheduler(CancelableScheduler cancelablescheduler) {
        this.cancelablescheduler = cancelablescheduler;
        return this;
    }


    /**
     * 设置线程池
     *
     * @param threadPoolexecutor
     * @return
     */
    public CraftEngineWrapper executor(ThreadPoolExecutor threadPoolexecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }


    /**
     * 添加Taskwrapper，可以添加多个Taskwrapper
     *
     * @param taskwrapper
     * @return
     */
    public CraftEngineWrapper putTaskWrapper(TaskWrapper taskwrapper) {
        this.taskWrappers.put(taskwrapper.getNo(), taskwrapper);
        return this;
    }

    @Override
    public CraftEngine build() {
        if (threadPoolExecutor == null) {
            throw new IllegalArgumentException("线程池不能为空");
        }
        CraftEngine craftEngine = new CraftEngine(cancelablescheduler, getTimeout(), threadPoolExecutor);
        craftEngine.setNo(getNo());
        craftEngine.setName(getName());
        craftEngine.setOnOff(true);

        //添加执行完成时的监听器
        getCompleteListeners().forEach(listener -> {
            craftEngine.addCompleteListener(listener);
        });
        //添加执行开始时的监听器
        getStartupListeners().forEach(listener -> {
            craftEngine.addStartupListener(listener);
        });
        //设置异常处理监听器
        craftEngine.setExceptionListener(getExceptionListener());

        Map<String, TaskHandler> taskHandlerMap = new HashMap<>();
        //bui1d工艺流中的所有task，并保存到map中
        for (Map.Entry<String, TaskWrapper> entry : taskWrappers.entrySet()) {
            TaskWrapper taskwrapper = entry.getValue();
            TaskHandler taskHandler = taskwrapper.build();
            taskHandler.setThreadPoolExecutor(threadPoolExecutor);
            taskHandler.setChain(craftEngine);
            taskHandlerMap.put(taskwrapper.getNo(), taskHandler);
        }
        //根据next解析所有task的依赖关系
        for (Map.Entry<String, TaskWrapper> entry : taskWrappers.entrySet()) {
            TaskWrapper taskWrapper = entry.getValue();
            Set<String> nextTaskNos = taskWrapper.getNextTaskWrappers();
            //循环设置next task与当前task的依赖关系
            for (String taskNo : nextTaskNos) {
                TaskWrapper nextWrapper = taskWrappers.get(taskNo);
                if (nextWrapper == null) {
                    throw new BussinessException("后续任务【" + taskNo + "】不存在");
                }
                //获取任务TaskHandler
                TaskHandler nextHandler = taskHandlerMap.get(nextWrapper.getNo());
                TaskHandler handler = taskHandlerMap.get(nextWrapper.getNo());
                //添加task之间的依赖关系
                addEdge(handler, nextHandler);
            }
        }
        //解析开始task与结束task
        for (Map.Entry<String, TaskHandler> entry : taskHandlerMap.entrySet()) {
            TaskHandler taskHandler = entry.getValue();
            //如果没有依赖的前置task，则为开始task，需要添加到head后续task集合中
            if (taskHandler.getDependTasks() == null || taskHandler.getDependTasks().size() == 0) {
                addEdge(craftEngine.head, taskHandler);
            }
            //如果没有next task，则为结束task，需要设置task的后续task为tai1
            if (taskHandler.getNextTasks() == null || taskHandler.getNextTasks().size() == 0) {
                addEdge(taskHandler, craftEngine.tail);
            }
        }
        return craftEngine;
    }

    /**
     * 添加依赖边
     */
    private void addEdge(TaskHandler from, TaskHandler to) {
        //把后续to task添加到from task的后续集合中
        from.addNextTask(to);
        //将from task添加到后续to task的依赖集合中
        to.addDependTask(from);
    }
}
