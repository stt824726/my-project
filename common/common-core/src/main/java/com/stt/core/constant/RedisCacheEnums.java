package com.stt.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: redis缓存key
 * @author: shaott
 * @create: 2024-04-24 11:05
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum RedisCacheEnums {

    ROUTE_JVM_RELOAD_TOPIC("ROUTE_JVM_RELOAD_TOPIC", "redis缓存路由topic");

    private String key;
    private String desc;
}
