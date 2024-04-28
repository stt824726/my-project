package com.stt.user.controller;

import com.stt.common.redis.api.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "test", tags = "测试类")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/hello")
    @ApiOperation(value = "测试接口", notes = "测试接口")
    public String hello(){
        redisUtil.getString("hello");
        redisUtil.setObject("hello","hello 123");
        return redisUtil.getString("hello");
    }

}
