package com.stt.workflow;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 组件加载器
 * @author: shaott
 * @create: 2024-05-08 19:59
 * @Version 1.0
 **/
@Slf4j
public class ComponentClassLoader extends URLClassLoader {
    //加载的类信息
    private Map<String,Class<?>> loadedClasses = new ConcurrentHashMap<>();

    public ComponentClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    };

    public ComponentClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }}

    /*获取类加载器中加载的所有类信息*/
    public Map<String, class<?>> getLoadedclasses() {
        return loadedclasses;
    }获取添加@configuration注解的类@return*/public List<Class> getconfigurationclassesOList<Class> configurationclasses = new ArrayList<>();for (Map.Entry<String, class<?>> entry/:loadedclasses.entryset(){class<?> clazz = entry.getvalue();if (springBeanutils.isconfiguration(clazz)){configurationclasses.add(clazz);海银行 2024-05-08 KF6797return configurationclasses;/*卸载类加载器中加载的类public void unloadclassO {/ /获取jar中的所有classloadedclasses.clear();

}
