package com.stt.workflow;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description: 组件加载类
 * @author: shaott
 * @create: 2024-05-08 19:42
 * @Version 1.0
 **/
@Slf4j
@Service
public class ComponentHotLoader {

    //组件jar包对应的类加载器，每个组件对应一个类加载器
    private final Map<String, JarclassLoader> jarclassLoaders = new HashMap<>();

    private final String mapperPathPattern = "classpath*:mapper/*Mapper.xml";

    @Value("${flow. engine. jarLocalpath}")
    public String jarLocalpath;//用于存放已经加载的ur1

    //组件jar包放置路径，默认放在classpath下的components目录下
    private String componentDirectory = "classpath:components";
    private SqlSessionFactoryBean sqlSessionFactoryBean;
    private SqlSessionFactory sqlSessionFactory;
    private PluginclassLoader pluginclassLoader;
    private final CopyOnWriteArraySet<URL> loadedurls = new CopyOnWriteArraySet<>();
    /**
     * 初始化插件classLoader
     */
    private void initO

    {
        if (pluginclassLoader == null) {
            synchronized (ComponentHotLoader.class) {
                if (pluginclassLoader == null) {
                    pluginclassLoader = new PluginclassLoader(Thread.currentThread().getContextClassLoader());
                }
            }
        }

    }

    public ComponentHotLoader() {
    }

    public ComponentHotLoader(String componentDirectory) {
        this.componentDirectory = componentDirectory;
    }
}