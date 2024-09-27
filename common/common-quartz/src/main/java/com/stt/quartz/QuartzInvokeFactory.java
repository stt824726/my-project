package com.stt.quartz;

import com.stt.quartz.entity.SysJob;
import com.stt.quartz.event.SysJobEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.Trigger;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @description:
 * @author: shaott
 * @create: 2024-09-26 15:24
 * @Version 1.0
 **/
@Aspect
@Slf4j
@AllArgsConstructor
public class QuartzInvokeFactory {

    private final ApplicationEventPublisher publisher;

    @SneakyThrows
    void publishJob(SysJob sysJob, Trigger trigger) {
        publisher.publishEvent(new SysJobEvent(sysJob, trigger));
    }
}
