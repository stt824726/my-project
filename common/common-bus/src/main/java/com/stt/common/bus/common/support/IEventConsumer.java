package com.stt.common.bus.common.support;

/**
 * @description: 事件消费者
 * @author: shaott
 * @create: 2024-01-10 17:33
 * @Version 1.0
 **/
public interface IEventConsumer<T extends BaseEvent> {

    /**
     * 消费事件
     * @param event
     */
    void consumer(T event);

}
