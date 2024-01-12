package com.stt.common.bus.common;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.util.concurrent.MoreExecutors;
import com.stt.common.bus.common.config.EventBusConfig;
import com.stt.common.bus.common.config.ResponseSynEventBus;
import com.stt.common.bus.common.support.DefaultSubscriberExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 事件bus配置
 * @author: shaott
 * @create: 2024-01-12 15:27
 * @Version 1.0
 **/
@Configuration
public class EventBusConfiguration {


    /**
     * 消息bus线程池配置
     * @return
     */
    @Bean(name = "eventBusExecutorService")
    public ExecutorService executorService(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20,50,60,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(500),new ThreadPoolExecutor.CallerRunsPolicy());
        return MoreExecutors.listeningDecorator(threadPoolExecutor);
    }


    /**
     * guava消息bus配置
     * @param executorService
     * @return
     */
    @Bean(name = "asyncEventBus")
    public AsyncEventBus asyncEventBus(@Qualifier("eventBusExecutorService") ExecutorService executorService){
        return new AsyncEventBus(executorService, new DefaultSubscriberExceptionHandler());
    }


    @Bean(name = "componentEventBus")
    public ResponseSynEventBus responseSynEventBus(@Qualifier("asyncEventBus") AsyncEventBus asyncEventBus){
        return new ResponseSynEventBus(asyncEventBus);
    }


    @Bean(name = "eventBusConfig")
    public EventBusConfig eventBusConfig(@Qualifier("componentEventBus") ResponseSynEventBus responseSynEventBus){
        return new EventBusConfig(responseSynEventBus);
    }


}
