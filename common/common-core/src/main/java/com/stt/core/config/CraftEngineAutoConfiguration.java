package com.stt.core.config;

import com.stt.core.constant.dto.CraftEngineProperties;
import com.stt.core.constant.dto.ExecuteThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-09 17:27
 * @Version 1.0
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(CraftEngineProperties.class)
@Order(1)
public class CraftEngineAutoConfiguration {

    @Autowired
    private CraftEngineProperties craftEngineProperties;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ExecuteThreadPoolProperties properties = craftEngineProperties.getThreadPool();
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(), properties.getMaximumPoolSize(), properties.getKeepAliveTime(), TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(properties.getBlockingQueueSize()), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}





