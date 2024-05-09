package com.stt.workflow.context;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.timer.TimeCounter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 节点执行数据
 * @author: shaott
 * @create: 2024-05-09 15:11
 * @Version 1.0
 **/
@Data
public class HandlerData {

    //节点实例编号
    private String no;

    //节点名成
    private String name;

    //运行状态
    private RunnableState runnablestate;

    //运行时间
    private TimeCounter timeCounter;

    //执行日志
    private String logs;

    public HandlerData() {
    }

    public HandlerData(String no, RunnableState runnablestate) {
        this(no, runnablestate, null);
    }

    public HandlerData(String no, RunnableState runnablestate, String logs) {
        this.no = no;
        this.runnablestate = runnablestate;
        this.logs = logs;
    }

    public LocalDateTime getstartTime() {
        return this.timeCounter.getStartTime();
    }

    public LocalDateTime getEndTime() {
        return this.timeCounter.getEndTime();
    }
}
