package com.stt.quartz.event;

import com.stt.quartz.entity.SysJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quartz.Trigger;

/**
 * @description:
 * @author: shaott
 * @create: 2024-09-26 15:46
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public class SysJobEvent {

    private final SysJob sysJob;

    private final Trigger trigger;
}
