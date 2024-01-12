package com.stt.common.bus.common.config;

import com.stt.common.bus.common.support.IEventConsumer;
import com.stt.core.util.springUtil.SpringBeanUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Map;

/**
 * @description:
 * @author: shaott
 * @create: 2024-01-11 08:44
 * @Version 1.0
 **/
public class EventBusConfig implements ApplicationRunner, DisposableBean {

    private ResponseSynEventBus responseSynEventBus;

    public EventBusConfig(ResponseSynEventBus responseSynEventBus){
        this.responseSynEventBus = responseSynEventBus;
    }

    @Override
    public void destroy() throws Exception {
        Map<String, IEventConsumer> beanMap = SpringBeanUtil.context.getBeansOfType(IEventConsumer.class);
        if(beanMap != null && !beanMap.isEmpty()){
            beanMap.forEach((k,v)->responseSynEventBus.removeConsumer(v));
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String,IEventConsumer> beanMap = SpringBeanUtil.context.getBeansOfType(IEventConsumer.class);
        if(beanMap != null && !beanMap.isEmpty()){
            beanMap.forEach((k,v)->responseSynEventBus.registerConsumer(v));
        }
    }
}
