package com.stt.quartz;

import com.stt.quartz.entity.SysJob;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: shaott
 * @create: 2024-09-26 15:22
 * @Version 1.0
 **/
@Slf4j
@DisallowConcurrentExecution
public class JobFactory implements Job {

    @Autowired
    private QuartzInvokeFactory quartzInvokeFactory;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext jobExecutionContext) {
        SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap()
                .get(QuartzEnum.SCHEDULE_JOB_KEY.getType());
        quartzInvokeFactory.publishJob(sysJob, jobExecutionContext.getTrigger());
    }
}
