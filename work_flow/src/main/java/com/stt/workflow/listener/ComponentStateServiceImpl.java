package com.stt.workflow.listener;

import com.stt.workflow.component.ComponentHandler;
import com.stt.workflow.constant.RunnableState;
import com.stt.workflow.context.Context;
import com.stt.workflow.context.HandlerData;
import com.stt.workflow.mapper.ExampleExecuteEntity;
import com.stt.workflow.mapper.ExampleExecuteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-10 10:24
 * @Version 1.0
 **/
@Slf4j
@Service
public class ComponentStateServiceImpl implements ComponentStateService {

    @Autowired
    private ExampleExecuteMapper exampleExecuteMapper;

    @Override
    public void onExecuteSuccess(Context context, HandlerData data) {
        ComponentHandler current = (ComponentHandler) context.getCurrent();
        log.info("组件成功运行完成,组件ID:{},组件N0:{},task:{}", current != null ? current.getComponentId() : "",
                current != null ? current.no() : ""
                , context.getParent() != null ? context.getParent().no() : "");
        updateComponentState(RunnableState.SUCCESS, context, data);
    }

    @Override
    public void onExecuteFail(Context context, HandlerData data) {
        ComponentHandler current = (ComponentHandler) context.getCurrent();
        log.info("组件运行失败,组件ID:{},组伴N0:{},task:{}", current != null ? current.getComponentId() : "",
                context.getParent() != null ? context.getParent().no() : "",
                current != null ? current.no() : "");
        updateComponentState(RunnableState.FAIL, context, data);
    }

    @Override
    public void onStop(Context context, HandlerData data) {
        updateComponentState(RunnableState.STOP, context, data);
    }

    @Override
    public void onSkip(Context context, HandlerData data) {
        log.info("跳过该组件,数据:{}", data.toString());
        updateComponentState(RunnableState.SKIP, context, data);
    }

    @Override
    public void onStart(HandlerData data) {
        log.info("开始组件,数据:", data.toString());
        updateComponentState(data.getRunnableState(), null, data);
    }

    /**
     * 更新组件状态信息
     *
     * @param runnablestate
     * @param context
     * @param data
     */
    private void updateComponentState(RunnableState runnablestate, Context context, HandlerData data) {
        log.info("component状态开始入库,数据:", data.toString());
        ExampleExecuteEntity entity = new ExampleExecuteEntity();
        entity.setId(Long.valueOf(data.getNo()));
        entity.setStatus(runnablestate.getCode());
        entity.setDeployStartTime(data.getstartTime());
        entity.setDeployEndTime(data.getEndTime());
        entity.setNodeLog(data.getLogs());
        exampleExecuteMapper.updateById(entity);
    }
}
