package com.stt.core.constant.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-09 17:09
 * @Version 1.0
 **/
@Data
@ConfigurationProperties(prefix = "flow.engine")
public class CraftEngineProperties {

    //组件的执行超时时间，单位:秒，默认1200秒 如果组件有自定义的超时时间，使用组件自定义超时时间
    private long componentTimeout =20*60* 1000L;

    //整个职责链的执行超时时间，单位:毫秒，默认30分钟
    private long chainTimeout =30 *60 * 1000L;

    //执行线程池的参数配置
    private ExecuteThreadPoolProperties threadPool = new ExecuteThreadPoolProperties();

}
