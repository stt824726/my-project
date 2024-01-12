package com.stt.common.bus.common.support;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 订阅者异常处理类
 * @author: shaott
 * @create: 2024-01-12 15:38
 * @Version 1.0
 **/
@Slf4j
public class DefaultSubscriberExceptionHandler implements SubscriberExceptionHandler {
    @Override
    public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
        log.error("消费消息出现异常",throwable);
    }
}
