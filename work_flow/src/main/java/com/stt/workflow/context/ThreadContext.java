package com.stt.workflow.context;

import com.stt.workflow.handler.AbstractHandler;
import lombok.Data;

/**
 * @description: 线程内部上下文
 * @author: shaott
 * @create: 2024-05-09 10:04
 * @Version 1.0
 **/
@Data
public class ThreadContext {

    /**
     * 当前节点对象
     */
    private AbstractHandler current;

    /**
     * 父节点对象
     */
    private AbstractHandler parent;

}
