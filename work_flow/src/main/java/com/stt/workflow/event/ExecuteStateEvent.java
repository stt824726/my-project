package com.stt.workflow.event;

import com.stt.workflow.constant.RunnableState;
import lombok.Data;

/**
 * @description: 组件执行结束事件
 * @author: shaott
 * @create: 2024-05-09 18:49
 * @Version 1.0
 **/
@Data
public class ExecuteStateEvent implements BaseEvent{

    //组件实例唯一编号
    private String instanceNo;

    //流水线的编号
    private String chainNo;

    //执行结果状态
    private RunnableState runnablestate;

    //执行结果日志
    private String logs;
}
