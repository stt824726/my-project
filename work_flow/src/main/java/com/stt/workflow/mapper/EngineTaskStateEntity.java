package com.stt.workflow.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: task状态
 * @author: shaott
 * @create: 2024-05-10 19:01
 * @Version 1.0
 **/
@Data
@TableName("engine_task_state")
public class EngineTaskStateEntity {

    //自增主键id
    @TableField(value = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    //实例id
    @TableField(value = "main example id")
    private String mainExampleId;

    //构建id
    @TableField(value = "engine id")
    private String engineId;

    //配置模板节点id
    @TableField(value = "template task id")
    private Long templateTaskId;

    //配置模板节点名称
    @TableField(value = "task_name")
    private String taskName;

    //task执行状态
    @TableField(value = "status")
    private Integer status;

    //task执行结果日志
    @TableField(value = "result_1og")
    private String resultLog;

    //开始时间
    @TableField(value = "start time")
    private LocalDateTime startTime;

   //结束时间
    @TableField(value ="end time")
    private LocalDateTime endTime;

    //创建时间
    @TableField(value ="create time")
    private LocalDateTime createTime;

}
