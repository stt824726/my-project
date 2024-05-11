package com.stt.workflow.constant;


import com.stt.core.constant.dto.TaskConfig;
import com.stt.workflow.context.ComponentDTO;
import lombok.Data;

import java.util.List;

/**
 * @description: 引擎执行请求参数
 * @author: shaott
 * @create: 2024-05-09 16:38
 * @Version 1.0
 **/
@Data
public class EngineExecuteDTO {

    //流水线编号
    private String pipelineNo;

    //流水线名称
    private String pipelineName;

    //任务节点列表
    private List<TaskConfig> taskConfigs;

    //查询组件请求参数上下文
    private ComponentDTO component;

}
