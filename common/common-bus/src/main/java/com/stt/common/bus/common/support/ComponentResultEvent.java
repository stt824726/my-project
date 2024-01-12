package com.stt.common.bus.common.support;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName 组件执行结果事件
 * @description:
 * @author: shaott
 * @create: 2024-01-09 19:24
 * @Version 1.0
 **/
@Data
public class ComponentResultEvent extends BaseEvent{

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private LocalDateTime endTime;

    /**
     * 执行结果描述
     */
    private String result;

    /**
     * 运行状态
     */
    private ComponentRunState componentRunState;

}
