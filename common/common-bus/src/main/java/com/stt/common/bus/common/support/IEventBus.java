package com.stt.common.bus.common.support;

/**
 * @ClassName
 * @description: 消费总线接口
 * @author: shaott
 * @create: 2024-01-10 17:14
 * @Version 1.0
 **/
public interface IEventBus<T extends BaseEvent> {

    /**
     * 发布事件
     * @param event
     */
    void post(T event);

    /**
     * 注册消费者
     * @param consumer
     */
    void registerConsumer(IEventConsumer consumer);

    /**
     * 移除消费者
     * @param consumer
     */
    void removeConsumer(IEventConsumer consumer);

}
