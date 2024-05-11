package com.stt.workflow.wrapper;

import com.stt.workflow.component.ComponentHandler;
import com.stt.workflow.component.ComponentManager;
import com.stt.workflow.handler.DefaultHandlerChain;
import com.stt.workflow.handler.HandlerChain;
import com.stt.workflow.handler.TaskHandler;
import com.stt.workflow.scheduler.CancelableScheduler;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: TaskHandler信息封装类
 * @author: shaott
 * @create: 2024-05-11 09:40
 * @Version 1.0
 **/
public class TaskWrapper extends Wrapper<TaskHandler> {

    //task责任链中的组件wrapper
    private List<ComponentWrapper> componentWrappers = new ArrayList<>();

    //后续的任务集合，集合中保存的是任务的编号n0
    private final Set<String> nextTaskWrappers = new HashSet();

    //可取消的调度任务，调度责任链的超时任务
    private CancelableScheduler cancelableScheduler;

    //组件管理器
    private ComponentManager componentManager;

    /**
     * 组件管理器
     *
     * @param componentManager
     */
    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }



    /**
     * 设置组件wrapper列表
     *
     * @param componentWrappers
     */
    public void setComponentWrappers(List<ComponentWrapper> componentWrappers) {
        if (componentWrappers == null) {
            return;
        }
        this.componentWrappers = componentWrappers;
    }

    /**
     * 增加组件wrapper
     *
     * @param wrapper
     * @return
     */
    public TaskWrapper addComponentWrapper(ComponentWrapper wrapper) {
        if (wrapper == null) {
            return this;
        }
        this.componentWrappers.add(wrapper);
        return this;
    }

    /**
     * 添加下一个节点列表wrapper
     *
     * @param taskNo
     * @return
     */
    public TaskWrapper addNextTaskWrapper(String taskNo) {
        if (StringUtils.isEmpty(taskNo)) {
            return this;
        }
        this.nextTaskWrappers.add(taskNo);
        return this;
    }


    /**
     * 设置可撤销的任务调度器
     *
     * @param cancelableScheduler
     */
    public void setCancelableScheduler(CancelableScheduler cancelableScheduler) {
        if (cancelableScheduler == null) {
            return;
        }
        this.cancelableScheduler = cancelableScheduler;
    }

    public Set<String> getNextTaskWrappers() {
        return nextTaskWrappers;
    }

    @Override
    public TaskHandler build() {
        if (StringUtils.isEmpty(getNo())) {
            throw new IllegalArgumentException("Task编号不能为空");
        }
        TaskHandler taskHandler = new TaskHandler();
        taskHandler.setNo(getNo());
        taskHandler.setName(getName());
        taskHandler.setHandlerInterceptor(getInterceptor());
        taskHandler.setOnOff(isOnoff());

        //添加执行完成时的监听器
        getCompleteListeners().forEach(listener -> {
            taskHandler.addCompleteListener(listener);
        });

        //添加执行开始时的监听器
        getStartupListeners().forEach(listener -> {
            taskHandler.addStartupListener(listener);
        });
        //设置异常处理监听器
        taskHandler.setExceptionListener(getExceptionListener());

        //设置TaskHandler的挂载的componentHandler链，根据list的顺序构建责任链
        if (componentWrappers != null && !componentWrappers.isEmpty()) {
            HandlerChain componentChain = new DefaultHandlerChain(cancelableScheduler, getTimeout());
            componentChain.setNo(getNo());
            componentWrappers.forEach(wrapper -> {
                ComponentHandler componentHandler = wrapper.build();
                //加入组件管理器
                componentManager.addComponent(wrapper.getNo(), componentHandler);
                componentHandler.setChain(componentChain);
                componentChain.append(componentHandler);
            });
            taskHandler.setComponentChain(componentChain);
        }
        return taskHandler;
    }
}
