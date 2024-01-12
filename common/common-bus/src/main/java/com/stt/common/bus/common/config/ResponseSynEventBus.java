package com.stt.common.bus.common.config;

import com.google.common.eventbus.AsyncEventBus;
import com.stt.common.bus.common.support.BaseEvent;
import com.stt.common.bus.common.support.IEventBus;
import com.stt.common.bus.common.support.IEventConsumer;

/**
 * @description: 消息同步事件
 * @author: shaott
 * @create: 2024-01-11 08:55
 * @Version 1.0
 **/
public class ResponseSynEventBus implements IEventBus {

    private AsyncEventBus asyncEventBus;

    public ResponseSynEventBus(AsyncEventBus asyncEventBus){
        this.asyncEventBus = asyncEventBus;
    }

    @Override
    public void post(BaseEvent event) {
        asyncEventBus.post(event);
    }

    @Override
    public void registerConsumer(IEventConsumer consumer) {
        asyncEventBus.register(consumer);
    }

    @Override
    public void removeConsumer(IEventConsumer consumer) {
        asyncEventBus.unregister(consumer);
    }

}
