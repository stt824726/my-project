package com.stt.workflow.listener;

import com.stt.workflow.context.HandlerData;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 10:23
 * @Version 1.0
 **/
public interface ComponentStateService extends StartupListener<HandlerData>,ExecuteCompleteListener<HandlerData>{
}
