package com.stt.workflow.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 17:20
 * @Version 1.0
 **/

@Data
@TableName("example_execute")
public class ExampleExecuteEntity {

    private static final long serialversionuID = 1L;

    /**
     * 自增主键id
     */
    @TableField(value = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实例id
     */
    @TableField(value = "main_example_id")
    private String mainExampleId;

    /**
     * 构建id
     */
    @TableField(value = "run_id")
    private String runId;

    /**
     * 配置模板节点id
     */
    @TableField(value = "node id")
    private Long nodeId;

    /**
     * 配置模板节点名称
     */
    @TableField(value = "node name")
    private String nodeName;

    /**
     * 配置模板节点组件id
     */
    @TableField(value = "node_class_id")
    private Long nodeClassId;

    /**
     * 配置模板节点名称
     */
    @TableField(value = "node class name")
    private String nodeClassName;


    /**
     * 组件id
     */
    @TableField(value = "component_no")
    private String componentNo;

    /**
     * 组件名称
     */
    @TableField(value = "component_name")
    private String componentName;

    /**
     * 组件执行状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 组件执行日志
     */
    @TableField(value = "node_1og")
    private String nodeLog;

    /**
     * 开始时间
     */
    @TableField(value = "deploy_start time")
    private LocalDateTime deployStartTime;

    /**
     * 结束时间
     */
    @TableField(value = "deploy_end _time")
    private LocalDateTime deployEndTime;

    /**
     * 创建时间
     */
    @TableField(value = "create time")
    private LocalDateTime createTime;
}
