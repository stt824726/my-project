package com.stt.workflow.listener;

import com.stt.workflow.context.HandlerData;

/**
 * @description: task节点业务逻辑
 * @author: shaott
 * @create: 2024-05-10 10:15
 * @Version 1.0
 **/
public interface TaskStateService extends StartupListener<HandlerData>,ExecuteCompleteListener<HandlerData>{
}
