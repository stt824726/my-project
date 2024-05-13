package com.stt.workflow.util;

import com.stt.common.bus.common.support.IEventProducer;
import com.stt.core.util.springUtil.SpringBeanUtil;
import com.stt.workflow.constant.ComponentConstant;
import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.event.ExecuteStateEvent;
import com.stt.workflow.exception.BussinessException;
import com.stt.workflow.handler.HandlerChain;
import com.stt.workflow.handler.ProcessorHandler;
import com.stt.workflow.wrapper.ComponentChainWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 组件工具类
 * @author: shaott
 * @create: 2024-05-10 11:00
 * @Version 1.0
 **/
@Slf4j
public class ComponentUtils {

    /**
     * 获取组件
     *
     * @param componentId
     * @return
     */
    public static HandlerChain getComponentDutyChain(String componentId) {
        String beanKey = componentId + ComponentConstant.DUTY_CHAIN;
        if (!SpringBeanUtil.contains(beanKey)) {
            throw new BussinessException("组件【“+componentId+”】未注册!");
        }
        List<String> processList = SpringBeanUtil.getBeanByType(beanKey, ArrayList.class);
        ComponentChainWrapper wrapper = new ComponentChainWrapper();
        wrapper.no(beanKey);
        //添加并生成组件责任链
        for (String str : processList) {
            ProcessorHandler handler = SpringBeanUtil.getBeanByType(str, ProcessorHandler.class);
            wrapper.addProcessHandler(handler);
        }
        return wrapper.build();
    }

    /**
     * 发送运行成功状态消息
     *
     * @param instanceNo
     */
    public static void sendStateMsgSuccess(String instanceNo) {
        sendStateMsg(instanceNo, RunnableState.SUCCESS, null);
    }

    /**
     * 发送运行失败状态消息
     *
     * @param instanceNo
     * @param errorMsg
     */
    public static void sendStateMsgError(String instanceNo, String errorMsg) {
        sendStateMsg(instanceNo, RunnableState.FAIL, errorMsg);
    }

    /**
     * 发送运行结果状态消息
     *
     * @param instanceNo
     * @param runnablestate
     * @param msg
     */
    public static void sendStateMsg(String instanceNo, RunnableState runnablestate, String msg) {
        log.info("组件开始发送状态消息:组件N0:{}, 状态:{}", instanceNo, runnablestate.getDescription());
        ExecuteStateEvent event = new ExecuteStateEvent();
        event.setInstanceNo(instanceNo);
        event.setRunnablestate(runnablestate);
        event.setLogs(msg);
        //获取事件提供者
        IEventProducer eventProducer = SpringBeanUtil.getBeanByType(IEventProducer.class);
        if (eventProducer == null) {
            throw new BussinessException("缺少事件提供者");
        }
        //发送事件消息
        eventProducer.post(event);
    }

}
