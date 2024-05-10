package com.stt.workflow.wrapper;

import com.stt.workflow.chain.ComponentChain;
import com.stt.workflow.handler.ProcessorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 组件责任链封装构建类，
 * 该构建类使用SPI从组件jar包中加载ProcessHandler，并依此组装成一个责任链，该责任链就是一个可执行的组件
 * @author: shaott
 * @create: 2024-05-10 11:21
 * @Version 1.0
 **/
public class ComponentChainWrapper extends Wrapper<ComponentChain> {

    //组件内部处理器列表
    private final List<ProcessorHandler> processorHandlers = new ArrayList<>();


    /**
     * 新增组件处理器
     *
     * @param handler
     * @return
     */
    public ComponentChainWrapper addProcessHandler(ProcessorHandler handler) {
        this.processorHandlers.add(handler);
        return this;
    }

    @Override
    public ComponentChain build() {
        //创建组件责任链
        ComponentChain chain = new ComponentChain(null, getTimeout());
        chain.setNo(getNo());
        chain.setName(getName());
        chain.setOnOff(true);
        //在组件责任链中添加处理器
        if (!processorHandlers.isEmpty()) {
            processorHandlers.forEach(processorHandler -> {
                processorHandler.setChain(chain);
                chain.append(processorHandler);
            });
        }
        return chain;
    }

}
