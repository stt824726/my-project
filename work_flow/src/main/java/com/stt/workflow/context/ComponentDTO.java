package com.stt.workflow.context;

import lombok.Data;

import java.util.Map;

/**
 * @description: 组件请求上下文
 * @author: shaott
 * @create: 2024-05-09 13:55
 * @Version 1.0
 **/
@Data
public class ComponentDTO {

    //交付流程阶段 1 DEV 2 SIT 3 UAT
    private String dtnStage;

    //服务模块ID
    private Long sysId;

    //自定义扩展参数
    private Map<String,String> extendComponentParams;
}
