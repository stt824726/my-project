package com.stt.common.bus.common.support;

/**
 * @description: 事件的生产者接口
 * @author: shaott
 * @create: 2024-05-07 16:52
 * @Version 1.0
 **/
public interface IEventProducer<T> {

    void post(T event);

}
