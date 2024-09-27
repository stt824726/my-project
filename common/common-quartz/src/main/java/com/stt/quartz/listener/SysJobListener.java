package com.stt.quartz.listener;

import com.stt.core.util.springUtil.SpringBeanUtil;
import com.stt.quartz.entity.SysJob;
import com.stt.quartz.event.SysJobEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Trigger;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * @description: 异步监听定时任务事件
 * @author: shaott
 * @create: 2024-09-26 16:09
 * @Version 1.0
 **/
@Slf4j
@AllArgsConstructor
public class SysJobListener {

    @Async
    @Order
    @EventListener(SysJobEvent.class)
    public void invokeJob(SysJobEvent event) {
        SysJob sysJob = event.getSysJob();
        try {
            SpringBeanUtil.invokeMethod(sysJob.getClassName(),sysJob.getMethodName(),sysJob.getMethodParamsValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
