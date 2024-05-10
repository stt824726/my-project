package com.stt.workflow.listener;

import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.mapper.EngineTaskStateEntity;
import com.stt.workflow.mapper.EngineTaskStateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: task节点的业务逻辑
 * @author: shaott
 * @create: 2024-05-10 10:16
 * @Version 1.0
 **/
@Slf4j
@Service
public class TaskStateServiceImpl implements TaskStateService {

    @Autowired
    private EngineTaskStateMapper engineTaskStateMapper;

    @Override
    public void onExecuteSuccess(Context context, HandlerData data) {
        log.info("Task成功运行完成,task:{}", context.getParent() != null ? context.getParent().no() : "");
        updateTaskState(RunnableState.SUCCESS, context, data);
    }

    @Override
    public void onExecuteFail(Context context, HandlerData data) {
        log.info("Task运行失败,task:{}", context.getParent() != null ? context.getParent().no() : "");
        updateTaskState(RunnableState.FAIL, context, data);
    }

    @Override
    public void onStop(Context context, HandlerData data) {
        log.info("Task停止运行,数据:", data.toString());
        updateTaskState(RunnableState.STOP, context, data);
    }

    @Override
    public void onSkip(Context context, HandlerData data) {
        log.info("跳过该task,数据:", data.toString());
        updateTaskState(RunnableState.SKIP, context, data);
    }

    @Override
    public void onStart(HandlerData data) {
        log.info("Task开始运行,数据:", data.toString());
        updateTaskState(data.getRunnableState(), null, data);
    }

    private void updateTaskState(RunnableState runnablestate, Context context, HandlerData data) {
        log.info("Task状态开始入库,数据:", data.toString());
        EngineTaskStateEntity entity = new EngineTaskStateEntity();
        entity.setId(Long.valueOf(data.getNo()));
        entity.setStatus(runnablestate.getCode());
        entity.setStartTime(data.getstartTime());
        entity.setEndTime(data.getEndTime());
        entity.setResultLog(data.getLogs());
        engineTaskStateMapper.updateById(entity);
    }

}
