package com.stt.workflow.core;

import com.stt.core.constant.dto.ComponentConfig;
import com.stt.core.constant.dto.TaskConfig;
import com.stt.workflow.component.ComponentInvoker;
import com.stt.workflow.component.ComponentManager;
import com.stt.workflow.component.InternalMethodComponentInvoker;
import com.stt.workflow.constant.EngineExecuteDTO;
import com.stt.workflow.context.Context;
import com.stt.workflow.handler.DefaultCraftEngineManager;
import com.stt.workflow.listener.EngineExecuteCompleteListener;
import com.stt.workflow.listener.ListenerManager;
import com.stt.workflow.wrapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 引擎启动器
 * @author: shaott
 * @create: 2024-05-09 09:39
 * @Version 1.0
 **/
@Slf4j
@Service
@Data
public class EngineLancer {

    /**
     * Task执行的线程池
     */
    private ThreadPoolExecutor taskThreadPoolExecutor;

    /**
     * 组件调用者
     */
    private ComponentInvoker componentInvoker = new InternalMethodComponentInvoker();

    //处理超时时间，单位:毫秒
    private Long timeout = 30 * 60 * 1000L;
    //组件的执行超时时间，单位:毫秒，默认20分钟。如果组件有自定义的超时时间，使用组件自定义超时时间
    private long componentTimeout = 20 * 60 * 1000L;
    //组件管理器
    private ComponentManager componentManager;

    //mybatis的sq1 session工程，组件数据库操作会用到
    private SqlSessionFactory sqlsessionFactory;

    //加载的数据库操作类信息
    private Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    //监听器管理器
    private ListenerManager listenerManager = new ListenerManager();

    public EngineLancer(ThreadPoolExecutor threadPoolExecutor) {
        this(threadPoolExecutor, null);
    }

    public EngineLancer(ThreadPoolExecutor threadPoolExecutor, ComponentInvoker invoker) {
        this(threadPoolExecutor, invoker, null);
    }

    public EngineLancer(ThreadPoolExecutor threadPoolexecutor, ComponentInvoker invoker, Long timeout) {
        if (threadPoolexecutor == null) {
            throw new IllegalArgumentException("参数错误，threadPoolExecutor不能为空");
        }
        this.taskThreadPoolExecutor = threadPoolexecutor;
        if (invoker != null) {
            this.componentInvoker = invoker;
        }
        if (timeout != null && timeout > 0) {
            this.timeout = timeout;
        }
    }


    /**
     * 创建并启动
     *
     * @param dto
     */
    public void startup(EngineExecuteDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        CraftEngine craftEngine = createEngine(dto);
        //添加流水线引擎完成监听器
        craftEngine.addCompleteListener(new EngineExecuteCompleteListener());
        Context context = new Context(dto.getComponent(), sqlsessionFactory, loadedClasses);
        context.setChain(craftEngine);
        //添加实例到管理器中
        DefaultCraftEngineManager.INSTANCE.addHandlerChain(craftEngine);
        CraftEngineThreadPoolFactory.threadPoolExecutor().submit(new EngineExecuteThread(craftEngine, context));
    }


    /**
     * 创建工艺流引擎实例
     *
     * @param dto
     * @return
     */
    protected CraftEngine createEngine(EngineExecuteDTO dto) {
        if (dto.getTaskConfigs() == null || dto.getTaskConfigs().isEmpty()) {
            throw new IllegalArgumentException("流水线配置中缺少task");
        }
        CraftEngineWrapper craftEnginewrapper = new CraftEngineWrapper();
        craftEnginewrapper.no(dto.getPipelineNo()).name(dto.getPipelineName()).timeout(this.timeout);
        craftEnginewrapper.executor(taskThreadPoolExecutor)
                .cancelableScheduler(CancelableSchedulerFactory.cancelablescheduler());
        craftEnginewrapper.setCompleteListeners(listenerManager.getEngineCompleteListeners());
        craftEnginewrapper.setStartupListeners(listenerManager.getEngineStartupListeners());
        craftEnginewrapper.setExceptionListener(listenerManager.getExceptionListener());
        //添加Taskwrapper到工艺流引擎wrapper中
        for (TaskConfig taskconfig : dto.getTaskConfigs()) {
            TaskWrapper taskwrapper = createTaskWrapper(taskconfig);
            craftEnginewrapper.putTaskWrapper(taskwrapper);
        }
        //构建工艺流的实例
        CraftEngine craftEngine = craftEnginewrapper.build();
        return craftEngine;

    }

    /**
     * 创建taskwrapper
     *
     * @param taskconfig
     * @return
     */
    protected TaskWrapper createTaskWrapper(TaskConfig taskconfig) {
        TaskWrapper taskwrapper = new TaskWrapper();
        taskwrapper.no(taskconfig.getTaskNo()).name(taskconfig.getTaskName());
        taskwrapper.setComponentManager(componentManager);
        taskwrapper.setCompleteListeners(listenerManager.getTaskCompleteListeners());
        taskwrapper.setStartupListeners(listenerManager.getTaskStartupListeners());
        taskwrapper.setExceptionListener(listenerManager.getExceptionListener());
        //设置后续taskwrapper
        String[] nextTaskNos = taskconfig.getNextTaskNo();
        if (nextTaskNos != null && nextTaskNos.length > 0) {
            for (String taskNo : nextTaskNos) {
                taskwrapper.addNextTaskWrapper(taskNo);

            }

        }
//添加componentwrapper
        List<ComponentConfig> componentconfigs = taskconfig.getComponentConfigs();
        if (componentconfigs != null && !componentconfigs.isEmpty()) {
            for (ComponentConfig componentconfig : componentconfigs) {
                ComponentWrapper componentWrapper = new ComponentWrapper();
                componentWrapper.no(componentconfig.getInstanceNo())
                        .name(componentconfig.getComponentName())
                        .onoff(componentconfig.getOnoff()).timeout(componentTimeout);
                componentWrapper.setComponentId(componentconfig.getComponentNo());
                componentWrapper.setComponentInvoker(componentInvoker);
                componentWrapper.setCompleteListeners(listenerManager.getComponentCompleteListeners());
                componentWrapper.setStartupListeners(listenerManager.getComponentStartupListeners());
                componentWrapper.setExceptionListener(listenerManager.getExceptionListener());
                taskwrapper.addComponentWrapper(componentWrapper);

            }
        }
        return taskwrapper;
    }


    /**
     * Engine执行线程
     */
    class EngineExecuteThread implements Runnable {
        private final CraftEngine craftEngine;
        private final Context context;

        public EngineExecuteThread(CraftEngine craftEngine, Context context) {
            this.craftEngine = craftEngine;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                craftEngine.start(context);
            } catch (Exception e) {
                listenerManager.getExceptionListener().onExecutedException(e, null, context);
            }
        }

    }
}
