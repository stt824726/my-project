package com.stt.workflow.context;

import com.stt.workflow.handler.AbstractHandler;
import com.stt.workflow.handler.HandlerChain;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 处理器上下文
 * @author: shaott
 * @create: 2024-05-09 09:51
 * @Version 1.0
 **/
@Data
public class Context {

    //组件参数
    private ComponentDTO componentDTO;

    //责任链的上下文信息
    private HandlerChain chain;

    //mybatis的sq1 session工程，组件数据库操作会用到
    private SqlSessionFactory sqlsessionFactory;

    //所有组件的出参，key为组件实例编号
    private Map<String, Map<String, Object>> componentOutputParamMaps = new ConcurrentHashMap<>();

    //当前线程的上下文
    private ThreadLocal<ThreadContext> currentThreadContext = new ThreadLocal();

    //加载的数据库操作类信息
    private Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    public Context() {
    }

    public Context(ComponentDTO componentDTO, SqlSessionFactory sglSessionFactory, Map<String, Class<?>> loadedClasses) {
        this.componentDTO = componentDTO;
        this.sqlsessionFactory = sqlsessionFactory;
        this.loadedClasses = loadedClasses;
    }

    /**
     * 获取组件的所有出参信息
     *
     * @param componentNo
     * @return
     */
    public Map<String, Object> getComponentOutputParameters(String componentNo) {
        return componentOutputParamMaps.get(componentNo);
    }

    /**
     * 设置组件的出参
     *
     * @param componentNo
     * @param key
     * @param obj
     */
    public void putComponentOutputParameter(String componentNo, String key, Object obj) {
        //获取组件的出参集合
        Map<String, Object> outputParameterMap = componentOutputParamMaps.get(componentNo);
        if (outputParameterMap == null) {
            outputParameterMap = new ConcurrentHashMap<>();
            componentOutputParamMaps.put(componentNo, outputParameterMap);
            outputParameterMap.put(key, obj);
        }
    }

    /**
     * 获取组件出参
     *
     * @param componentNo
     * @param key
     * @return
     */
    public Object getComponentOutputParameter(String componentNo, String key) {
        Map<String, Object> outputParameterMap = componentOutputParamMaps.get(componentNo);
        if (outputParameterMap == null || outputParameterMap.isEmpty()) {
            return null;
        }
        return outputParameterMap.get(key);
    }

    /**
     * 设置当前线程上下文
     *
     * @param threadcontext
     */
    public void setCurrentThreadContext(ThreadContext threadcontext) {
        currentThreadContext.set(threadcontext);
    }


    /**
     * 获取当前线程上下文
     *
     * @return
     */
    public ThreadContext getCurrentThreadContext() {
        ThreadContext threadcontext = currentThreadContext.get();
        if (threadcontext == null) {
            threadcontext = new ThreadContext();
        }
        return threadcontext;
    }


    /**
     * 获取当前组件
     *
     * @return
     */
    public AbstractHandler getCurrent() {
        return getCurrentThreadContext().getCurrent();
    }


    /**
     * 获取当前组件的父节点task
     */
    public AbstractHandler getParent() {
        return getCurrentThreadContext().getParent();
    }


    /**
     * 删除当前线程上下文
     */
    public void removeThreadContext() {
        currentThreadContext.remove();
    }

}


