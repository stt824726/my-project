package com.stt.workflow.component;

import com.stt.workflow.context.Context;

/**
 * @description: 组件调用者
 * @author: shaott
 * @create: 2024-05-09 09:46
 * @Version 1.0
 **/
public interface ComponentInvoker {

    /**
     * 调用组件
     * @param component
     * @param context
     * @return
     */
    Object invoke(ComponentHandler component, Context context);

    /**
     * 停止组件
     * @param component
     * @param context
     */
    void stop(ComponentHandler component,Context context);

}
