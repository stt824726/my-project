package com.stt.workflow.classLoad;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 插件类加载器
 * @author: shaott
 * @create: 2024-05-10 09:40
 * @Version 1.0
 **/
@Slf4j
@Data
public class PluginClassLoader extends URLClassLoader {

    //加载的类信息
    private Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    //用于存放所有加载过的jar包最后变更时间
    private Map<String, Long> lastModifyTimesMap = new ConcurrentHashMap<>();

    public PluginClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
