package com.stt.core.constant.dto;

import lombok.Data;

import java.util.List;

/**
 * @description: 工艺流节点配置
 * @author: shaott
 * @create: 2024-05-09 16:50
 * @Version 1.0
 **/
@Data
public class TaskConfig {

    //task的编号
    private String taskNo;

    //task的名称
    private String taskName;

    //前task的编号
    private String[] previousTaskNo;

    //后task的编号
    private String[] nextTaskNo;

    //task是否必须处理完 true:是，false:否

    private Boolean reguired;

    //节点包含的组件，一个task可以包含多个组件
    private List<ComponentConfig> componentConfigs;

}
