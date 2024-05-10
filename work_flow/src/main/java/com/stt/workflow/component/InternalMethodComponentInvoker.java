package com.stt.workflow.component;

import com.stt.workflow.context.Context;
import com.stt.workflow.handler.HandlerChain;
import com.stt.workflow.util.ComponentUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 组件调用者内部方法调用，根据组件编号以及参数信息从spring boot的容器中获取组件并调用
 * @author: shaott
 * @create: 2024-05-10 10:50
 * @Version 1.0
 **/
@Slf4j
public class InternalMethodComponentInvoker implements ComponentInvoker {

    @Override
    public Object invoke(ComponentHandler component, Context context) {
        log.info("开始调用组件运行，组件ID:{}，组件N0:{}", component.getComponentId(), component.no());
        //获取组件的dutychain
        HandlerChain dutyChain = ComponentUtils.getComponentDutyChain(component.getComponentId());
        //启动执行组件
        dutyChain.start(context);
        log.info("组件运行完成，组件ID:{}，组件NO:{}，状态:{}", component.getComponentId(), component.no(), dutyChain.getRunnableState());
        //返回组件执行状态
        return dutyChain.getRunnableState();
    }


    @Override
    public void stop(ComponentHandler component, Context context) {
        log.info("开始停止组件运行，组件ID:{}，组件N0:{}", component.getComponentId(), component.no());
        //获取组件的dutychain
        HandlerChain dutyChain = ComponentUtils.getComponentDutyChain(component.getComponentId());
        //停止执行组件
        dutyChain.stop();
        log.info("组件运行停止，组件ID:{}，组件NO:{}，状态:{}", component.getComponentId(),component.no(),dutyChain.getRunnableState());
    }
}
