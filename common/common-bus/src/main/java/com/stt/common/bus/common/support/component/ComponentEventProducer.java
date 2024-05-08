package com.stt.common.bus.common.support.component;

import com.stt.common.bus.common.support.IEventBus;
import com.stt.common.bus.common.support.IEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-07 16:55
 * @Version 1.0
 **/
@Slf4j
@Service
public class ComponentEventProducer implements IEventProducer<ComponentExecuteEvent> {

    @Resource
    private IEventBus<ComponentExecuteEvent> eventBus;

    @Override
    public void post(ComponentExecuteEvent event) {
        log.debug("组件执行事件｛｝",event.getTaskName());
        eventBus.post(event);
    }
}
