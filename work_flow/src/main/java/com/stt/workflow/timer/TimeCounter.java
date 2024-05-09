package com.stt.workflow.timer;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @description: 责任链时间处理器
 * @author: shaott
 * @create: 2024-05-09 10:35
 * @Version 1.0
 **/
@Data
public class TimeCounter {
    //开始时间戳，单位:毫秒
    private long startTime;

    //结束时间戳，单位:毫秒
    private long endTime;

    //开始计时
    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    //结束计时
    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    //花费时间，单位:秒
    public long time() {
        return (this.endTime - this.startTime) / 1000;
    }

    //获取开始时间
    public LocalDateTime getStartTime() {
        if (this.startTime <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(this.startTime).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
    }

    //获取结束时间
    public LocalDateTime getEndTime() {
        if (this.endTime <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(this.endTime).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
    }
}
