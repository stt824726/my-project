package com.stt.workflow.core;

import com.stt.workflow.component.ComponentInvoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 引擎启动器
 * @author: shaott
 * @create: 2024-05-09 09:39
 * @Version 1.0
 **/
@Slf4j
@Service
public class EngineLancer {

    /**
     * Task执行的线程池
     */
    private ThreadPoolExecutor taskThreadPoolExecutor;

    /**
     * 组件调用者
     */
    private final ComponentInvoker componentInvoker = new InternalMethodcomponentInvoker();

    //处理超时时间，单位:毫秒
    private final Long timeout = 30 * 60 * 1000L;
    //组件的执行超时时间，单位:毫秒，默认20分钟。如果组件有自定义的超时时间，使用组件自定义超时时间
    private final long componentTimeout = 20 * 60 * 1000L;
    //组件管理器
    private ComponentManager componentManager;

    //mybatis的sq1 session工程，组件数据库操作会用到
    private SqlSessionFactory sqlsessionFactory;

    //加载的数据库操作类信息
    private final Map<String, Class<?>> loadedclasses = new ConcurrentHashMap◇();

    //监听器管理器
    private final ListenerManager listenermanager = new ListenerManager();

    public EngineLancer(ThreadPoolExecutor threadPoolExecutor) {
        this(threadPoolExecutor, null);
    }
}
