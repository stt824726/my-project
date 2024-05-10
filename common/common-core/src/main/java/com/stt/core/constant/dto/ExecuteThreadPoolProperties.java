package com.stt.core.constant.dto;

import lombok.Data;

/**
 * @description: 流水线执行模块的执行线程池配置
 * @author: shaott
 * @create: 2024-05-09 17:12
 * @Version 1.0
 **/
@Data
public class ExecuteThreadPoolProperties {

    //线程池核心线程数，默认:100
    private int corePoolSize = 100;

    //线程池最大线程数，默认:1000
    private int maximumPoolSize = 1000;

    //线程池中的空闲线程保持存活时间，单位:秒，默认:30秒
    private long keepAliveTime = 30;

    //阻塞队列的大小，默认:500
    private int blockingQueueSize = 500;

}
