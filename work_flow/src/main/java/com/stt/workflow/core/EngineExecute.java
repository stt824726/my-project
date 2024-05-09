package com.stt.workflow.core;

import com.stt.core.constant.dto.CraftEngineProperties;
import com.stt.core.constant.dto.EngineExecuteDTO;
import com.stt.core.util.Result;
import com.stt.core.util.common.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 引擎执行逻辑类
 * @author: shaott
 * @create: 2024-05-09 16:24
 * @Version 1.0
 **/
@Slf4j
@Service
public class EngineExecute {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private CraftEngineProperties craftEngineProperties;

    @Autowired
    private ComponentManager componentManager;

    @Autowired
    private EngineStateService engineStateService;

    @Autowired
    private TaskStateService taskStateService;

    @Autowired
    private ComponentStateService componentStateService;

    public Result execute(EngineExecuteDTO engineExecuteDTO){
        log.info("引擎启动开始:{}", JacksonUtil.writeString(engineExecuteDTO));

    }

}
