package com.stt.workflow.handler;

import com.stt.workflow.context.Context;

/**
 * @description: 流程处理器链抽象类
 * @author: shaott
 * @create: 2024-05-09 13:55
 * @Version 1.0
 **/
public abstract class HandlerChain extends AbstractHandler {

    /**
     * 添加处理器到处理链的最后面
     * @param handler
     * @return
     */
    public abstract HandlerChain append(NodeHandler handler);

    /**
     * 启动职责链，执行每个节点的任务
     * @param request
     */
    public abstract void start(Context request);

}
