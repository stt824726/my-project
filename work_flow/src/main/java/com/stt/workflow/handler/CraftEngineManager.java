package com.stt.workflow.handler;

import java.util.Map;

/**
 * @description: 责任链管理器接口
 * @author: shaott
 * @create: 2024-05-11 11:26
 * @Version 1.0
 **/
public interface CraftEngineManager {

    /**
     * 添加handler链到管理器中
     * @param chain
     */
    void addHandlerChain(HandlerChain chain);

    /**
     * 根据handler链的编导查询链对象
     * @param chainNo
     * @return
     */
    HandlerChain getHandlerChain(String chainNo);


    /**
     * 获取所有handler链
     * @return
     */
    Map<String,HandlerChain> getHandlerChains();

    /**
     * 删除handler链。并且将链的状态更新成stop
     * @param chainNo
     */
    void remove(String chainNo);

    /**
     * 删除handler链 保持链的最终状态为最后一个节点的终态
     * @param chainNo
     */
    void stop(String chainNo);
}
