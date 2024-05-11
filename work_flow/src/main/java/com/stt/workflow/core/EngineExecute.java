package com.stt.workflow.core;

import com.stt.core.constant.dto.CraftEngineProperties;

import com.stt.core.util.Result;
import com.stt.core.util.common.JacksonUtil;
import com.stt.core.util.springUtil.SpringBeanUtil;
import com.stt.workflow.constant.EngineExecuteDTO;
import com.stt.workflow.context.ComponentHotLoader;
import com.stt.workflow.component.ComponentManager;
import com.stt.workflow.listener.ComponentStateService;
import com.stt.workflow.listener.EngineStateService;
import com.stt.workflow.listener.ListenerManager;
import com.stt.workflow.listener.TaskStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 引擎执行逻辑类
 * @author: shaott
 * @create: 2024-05-09 16:24
 * @Version 1.0
 **/
@Slf4j
@Service
public class EngineExecute {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private CraftEngineProperties craftEngineProperties;

    @Autowired
    private ComponentManager componentManager;

    @Autowired
    private EngineStateService engineStateService;

    @Autowired
    private TaskStateService taskStateService;

    @Autowired
    private ComponentStateService componentStateService;

    public Result execute(EngineExecuteDTO engineExecuteDTO){
        log.info("引擎启动开始:{}", JacksonUtil.writeString(engineExecuteDTO));
        //创建工艺流引擎
        EngineLancer craftEngineLancer = new EngineLancer(threadPoolExecutor,null,craftEngineProperties.getChainTimeout());
        //设置组件数据库访问上下文
        ComponentHotLoader componentHotLoader = SpringBeanUtil.getBean(ComponentHotLoader.class);
        craftEngineLancer.setSqlsessionFactory(componentHotLoader.getSqlSessionFactory());
        //设置组件数据库操作的类相关信息
        craftEngineLancer.setLoadedClasses(componentHotLoader.getPluginClassLoader().getLoadedClasses());
        //设置组件管理器
        craftEngineLancer.setComponentManager(componentManager);
        //设置组件的执行超时时间
        craftEngineLancer.setComponentTimeout(craftEngineProperties.getComponentTimeout());
        //设置监听器
        ListenerManager listenerManager = new ListenerManager();
        listenerManager.addEngineCompleteListener(engineStateService);
        listenerManager.addEngineStartupListener(engineStateService);
        listenerManager.addTaskCompleteListener(taskStateService);
        listenerManager.addTaskStartupListener(taskStateService);
        listenerManager.addComponentCompleteListener(componentStateService);
        listenerManager.addComponentStartupListener(componentStateService);
        craftEngineLancer.setListenerManager(listenerManager);
        //启动
        craftEngineLancer.startup(engineExecuteDTO);
        return Result.ok();
    }

}
