package com.stt.workflow.listener;

import com.stt.workflow.context.HandlerData;

/**
 * @description: 引擎状态业务逻辑
 * @author: shaott
 * @create: 2024-05-10 10:12
 * @Version 1.0
 **/
public interface EngineStateService extends StartupListener<HandlerData>,ExecuteCompleteListener<HandlerData>{

}
