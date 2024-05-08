package com.stt.common.bus.common.config;

import com.google.common.eventbus.AsyncEventBus;
import com.stt.common.bus.common.support.BaseEvent;
import com.stt.common.bus.common.support.IEventBus;
import com.stt.common.bus.common.support.IEventConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @description: 消息同步事件
 * @author: shaott
 * @create: 2024-01-11 08:55
 * @Version 1.0
 **/
public class ResponseSynEventBus implements IEventBus, ApplicationRunner, ApplicationContextAware, DisposableBean {

    private AsyncEventBus asyncEventBus;

    private ApplicationContext applicationContext;



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

    /**
     * 启动时，扫描消费者
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map consumerMap = applicationContext.getBeansOfType(IEventConsumer.class);
        if(consumerMap == null || consumerMap.isEmpty()){
            return;
        }
        consumerMap.forEach((k,v)->{
            asyncEventBus.register(v);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        Map consumerMap = applicationContext.getBeansOfType(IEventConsumer.class);
        if(consumerMap == null || consumerMap.isEmpty()){
            return;
        }
        consumerMap.forEach((k,v)->{
            asyncEventBus.unregister(v);
        });
    }
}
