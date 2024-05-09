package com.stt.workflow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 组件运行状态
 * @author: shaott
 * @create: 2024-05-09 10:19
 * @Version 1.0
 **/
@AllArgsConstructor
@Getter
public enum RunnableState {

  //待运行,未开始
    WAIT_RUN(0,"等待"),
//运行中
    RUNNING(1,"进行中"),
//运行成功
    SUCCESS(2,"成功"),
//跳过不运行
    SKIP(3,"跳过"),
//运行失败
    FAIL(-1,"失败"),
//停止运行
    STOP(-2,"停止");

    private int code;

    private String description;

}
