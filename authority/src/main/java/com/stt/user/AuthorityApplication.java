package com.stt.user;

import com.stt.common.redis.annotation.EnableRedis;
import com.stt.swagger.annotation.EnableSwagger2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableSwagger2
@EnableRedis
public class AuthorityApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthorityApplication.class).run(args);
    }
}
