package com.stt.workflow.handler;

import com.stt.workflow.context.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 19:50
 * @Version 1.0
 **/
@Slf4j
public class MultiNextStartHandler extends TaskHandler {

    public MultiNextStartHandler() {

    }

    @Override
    public String name() {
        return "multi next start handler";
    }

    @Override
    public boolean onHandle(Context context) {
        log.info("craft engine execute startup.");
        return true;
    }

}
