package com.stt.gateway.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serializable;

/**
 * @description: 路由配置信息
 * @author: shaott
 * @create: 2024-04-24 19:43
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteDefinitionVo extends RouteDefinition implements Serializable {

    /**
     * 路由名称
     */
    private String routeName;

}
