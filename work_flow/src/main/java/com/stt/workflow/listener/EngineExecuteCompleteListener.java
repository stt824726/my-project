package com.stt.workflow.listener;

import com.stt.workflow.context.Context;
import com.stt.workflow.handler.DefaultCraftEngineManager;

/**
 * @description: 工艺流引擎流程执行完成后处理
 * @author: shaott
 * @create: 2024-05-11 11:18
 * @Version 1.0
 **/
public class EngineExecuteCompleteListener implements ExecuteCompleteListener {

    @Override
    public void onExecuteSuccess(Context context, Object data) {
//执行完成后从管理器中删除
        DefaultCraftEngineManager.INSTANCE.remove(context.getChain().no());
    }

    @Override
    public void onExecuteFail(Context context, Object data) {
//执行完成后从管理器中删除
        DefaultCraftEngineManager.INSTANCE.remove(context.getChain().no());
    }

    @Override
    public void onStop(Context context, Object data) {
//执行完成后从管理器中删除
        DefaultCraftEngineManager.INSTANCE.remove(context.getChain().no());
    }
}
