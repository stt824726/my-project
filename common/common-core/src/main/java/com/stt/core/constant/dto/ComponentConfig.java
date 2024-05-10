package com.stt.core.constant.dto;

import lombok.Data;

/**
 * @description: 节点组件配置
 * @author: shaott
 * @create: 2024-05-09 16:59
 * @Version 1.0
 **/
@Data
public class ComponentConfig {

    //组件编导
    private String componentNo;

    //组件名称
    private String componentName;

    //当前实例的唯一编号
    private String instanceNo;

    //组件开关，是否运行该组件，true:运行，false:不运行，默认不运行
    private Boolean onoff;

    //组件序号
    private Integer serialNo;
}
