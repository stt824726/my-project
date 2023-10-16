package com.stt.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "test", tags = "测试类")
public class TestController {

    @GetMapping("/hello")
    @ApiOperation(value = "测试接口", notes = "测试接口")
    public String hello(){
        return "hello user";
    }

}
