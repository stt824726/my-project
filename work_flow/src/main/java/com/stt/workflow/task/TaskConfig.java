package com.stt.workflow.task;

import com.stt.core.constant.dto.ComponentConfig;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 10:51
 * @Version 1.0
 **/
@Data
public class TaskConfig {

    /**
     * task的编号
     */
            private String taskNo;

    /**
     * task的名称
     */
    private String taskName;

    /**
     * 前task的编号
     */
    private String[] previousTaskNo;

    /**
     * 后task的编号
     */
            private String[] nextTaskNo;

    /**
     * task是否必须处理完
     */
    private Boolean reguired;

    /**
     * 节点包含的组件，一个task可以包含多个组件
     */
     private List<ComponentConfig> componentConfigs;

}
