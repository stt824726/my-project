package com.stt.workflow.handler;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;

/**
 * @description: 流程节点处理器接口
 * @author: shaott
 * @create: 2024-05-09 10:09
 * @Version 1.0
 **/
public interface NodeHandler {

    /**
     * 获取处理器名称
     * @return
     */
    String name();

    /**
     * 获取处理器编号
     * @return
     */
    String no();

    /**
     * 阶段请求处理
     * @param request
     */
    void handle(Context request);

    /**
     * 处理器是否必须处理完成，才能执行下一步
     */
    boolean isRequired();

    /**
     * 停止handler的执行
     */
    void stop();

    /**
     * handler执行完
     * @param runnableState
     * @param data
     */
    void complete(RunnableState runnableState, Object data);

    /**
     * 设置下个阶段处理器
     * @param stageHandler
     */
    void setNextHandler(NodeHandler stageHandler);

}
