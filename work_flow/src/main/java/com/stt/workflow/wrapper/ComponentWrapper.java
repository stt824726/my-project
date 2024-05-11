package com.stt.workflow.wrapper;

import com.stt.workflow.component.ComponentHandler;
import com.stt.workflow.component.ComponentInvoker;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 组件handler封装类
 * @author: shaott
 * @create: 2024-05-11 10:01
 * @Version 1.0
 **/
@Data
public class ComponentWrapper extends Wrapper<ComponentHandler> {

    //组件调用者
    ComponentInvoker componentInvoker;
    //组件ID
    private String componentId;

    public ComponentInvoker getComponentInvoker() {
        return componentInvoker;
    }

    public void setComponentInvoker(ComponentInvoker componentInvoker) {
        this.componentInvoker = componentInvoker;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }


    /**
     * 构建Hand1er对象
     *
     * @return
     */
    public ComponentHandler build() {
        if (StringUtils.isEmpty(getNo())) {
            throw new IllegalArgumentException("组件编号不能为空");
        }
        if (componentInvoker == null) {
            throw new IllegalArgumentException("组件调用者componentInvoker不能为");
        }
        ComponentHandler componentHandler = new ComponentHandler();
        componentHandler.setComponentId(componentId);
        componentHandler.setNo(getNo());
        componentHandler.setName(getName());
        componentHandler.setTimeout(getTimeout());
        componentHandler.setOnOff(isOnoff());
        componentHandler.setHandlerInterceptor(getInterceptor());
        componentHandler.setComponentInvoker(componentInvoker);
        //添加执行完成时的监听器
        getCompleteListeners().forEach(listener -> {
            componentHandler.addCompleteListener(listener);
        });
        //添加执行开始时的监听器
        getStartupListeners().forEach(istener -> {
            componentHandler.addStartupListener(istener);
        });
        //设置异常处理监听器
        componentHandler.setExceptionListener(getExceptionListener());
        return componentHandler;
    }
}
