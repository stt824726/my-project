package com.stt.workflow.component;

import java.util.Map;

/**
 * @description: 组件管理接口，管理所有正在执行和未执行的组件，执行过的组件会从管理器中删除
 * @author: shaott
 * @create: 2024-05-10 10:30
 * @Version 1.0
 **/
public interface ComponentManager {

    /**
     * 添加组件
     * @param componentInstanceNo
     * @param componentHandler
     */
    void addComponent(String componentInstanceNo, ComponentHandler componentHandler);

    /**
     * 查询所有组件
     */
    Map<String,ComponentHandler> getALLComponents();

    /**
     * 查询组件信息
     * @param componentInstanceNo
     * @return
     */
    ComponentHandler getComponent(String componentInstanceNo);

    /**
     * 组件数量
     * @return
     */
    int size();
}
