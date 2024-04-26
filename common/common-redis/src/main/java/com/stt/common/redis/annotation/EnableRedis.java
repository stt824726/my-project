package com.stt.common.redis.annotation;

import com.stt.common.redis.configuration.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @description: redis缓存开启
 * @author: shaott
 * @create: 2024-04-26 11:21
 * @Version 1.0
 **/
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ RedisAutoConfiguration.class })
public @interface EnableRedis {

}
