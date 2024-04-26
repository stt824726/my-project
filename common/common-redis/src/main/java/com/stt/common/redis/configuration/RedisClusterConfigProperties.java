package com.stt.common.redis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: shaott
 * @create: 2024-04-26 14:00
 * @Version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisClusterConfigProperties {

    private List<String> nodes;

    private Integer maxAttempts;

    private Integer connectionTimeout;

    private Integer soTimeout;

    private String password;

}
