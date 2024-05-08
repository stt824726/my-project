package com.stt.common.bus.common.support.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ComponentRunState
 * @description:
 * @author: shaott
 * @create: 2024-01-09 19:56
 * @Version 1.0
 **/
@AllArgsConstructor
@Getter
public enum ComponentRunState {

    INIT("INIT","初始化"),
    RUNNING("RUNNING","运行中"),
    STOP("STOP","中止"),
    SUCCESS("SUCCESS","运行成功"),
    FAIL("FAIL","运行失败")
    ;

    private String code;

    private String desc;
}
