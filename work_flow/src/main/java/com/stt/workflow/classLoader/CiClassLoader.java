package com.stt.workflow.classLoader;

import com.stt.core.util.springUtil.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 17:38
 * @Version 1.0
 **/
@Slf4j
public class CiClassLoader extends URLClassLoader {

    //加载的类信息
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    public CiClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public CiClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * 获取类加载器中加载的所有类信息
     */
    public Map<String, Class<?>> getLoadedClasses() {
        return loadedClasses;
    }

    /**
     * 获取添加@configuration注解的类
     */
    public List<Class> getConfigurationClasses() {
        List<Class> configurationClasses = new ArrayList();
        for (Map.Entry<String, Class<?>> entry : loadedClasses.entrySet()) {
            Class<?> clazz = entry.getValue();
            if (SpringBeanUtil.isConfiguration(clazz)) {
                configurationClasses.add(clazz);
            }
        }
        return configurationClasses;
    }


    /**
     * 卸载类加载器中加载的类
     */
    public void unloadClass() {
        //获取iar中的所有class
        loadedClasses.clear();
    }


    /**
     * 发现class，父类的1oadclass会调用
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //从已加载的类集合中获取指走名称的类
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        //调用父类URLC1assLoader中的findclass方法加载指定名称的类
        clazz = super.findClass(name);
        //将URLC1assLoader中加载的类添加到类集合中
        loadedClasses.put(name, clazz);
        return clazz;
    }

    /**
     * 根据类路径获取bean名称
     */
    private String getBeanName(String clazzName) {
        String[] beanNames = clazzName.split("\\.");
        return StringUtils.uncapitalize(beanNames[beanNames.length - 1]);
    }

}
