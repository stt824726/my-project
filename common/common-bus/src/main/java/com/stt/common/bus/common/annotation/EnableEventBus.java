package com.stt.common.bus.common.annotation;

import com.stt.common.bus.common.EventBusConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @description: 开启消息bus
 * @author: shaott
 * @create: 2024-01-12 15:09
 * @Version 1.0
 **/
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableAutoConfiguration
@Import(EventBusConfiguration.class)
public @interface EnableEventBus {


}
