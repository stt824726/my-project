package com.stt.common.redis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: shaott
 * @create: 2024-04-28 17:31
 * @Version 1.0
 **/
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonProperties {
    private String host;

    private String port;

    private String password;

    private String address;

    private Integer timeout;

    private Integer database;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize=10;

}
