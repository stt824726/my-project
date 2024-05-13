package com.stt.workflow.classLoader;

import com.stt.core.util.springUtil.SpringBeanUtil;
import com.stt.workflow.constant.PluginOn;
import com.stt.workflow.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.stt.workflow.constant.ComponentConstant.DUTY_CHAIN;

enum LoadState {
    WAIT_LOAD("待加载"),
    LOADING("正在加载"),
    LOADED("加载完成");

    private final String description;

    LoadState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-11 17:55
 * @Version 1.0
 **/
@Slf4j
public class JarClassLoader implements Closeable {

    //注册到spring容器中的Bean的名称，key为class全路径名(clazz.getName())，value为bean的名称(beanName)
    private final List<String> springBeanNames = new ArrayList<>();
    //注册到5pring容器中的Bean的名称，该Bean只是注册了bean对象，而没有注册bean定义
    private final List<String> notBeanDefinitionList = new ArrayList();
    //加载jar包使用的类加载器
    private CiClassLoader classLoader;
    //加载的jar包的更新时间
    private Long loadTimeStamp;
    //iar包加载状态，默认为等待加载，
    private LoadState state = LoadState.WAIT_LOAD;

    public JarClassLoader(CiClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public JarClassLoader(URL url) {
        this(new CiClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader()));
    }

    public JarClassLoader(File jarFile) throws MalformedURLException {
        this(jarFile.toURI().toURL());
    }

    public void setJarFile(File jarFile) throws MalformedURLException {
        this.classLoader = new CiClassLoader(new URL[]{jarFile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
    }

    public void loadJar(File file, List<Long> componentIds, String jarName) throws Exception {
        this.state = LoadState.LOADING;

        //加载jar包中的所有文件
        try (JarFile jarFile = new JarFile(file.getAbsolutePath())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().equals("dutyChain.txt")) {
                    log.info("load dutyChain.txt {}", jarEntry.getName());
                    loadComponentToBean(jarFile, jarEntry, componentIds, jarName);
                    continue;
                }
                if (!jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                String className = jarEntry.getName().replace("/", ".").replace(".class", "");
                log.info("load class {}", className);
                //使用指走的类加载器加载类
                Class<?> clazz = classLoader.loadClass(className);
            }
            //把添加了spring注解的类，注册到spring容器中
            Map<String, Class<?>> classesMap = classLoader.getLoadedClasses();
            for (Map.Entry<String, Class<?>> entry : classesMap.entrySet()) {
                String clazzName = entry.getKey();
                Class<?> clazz = entry.getValue();
                if (isPluginBean(clazz)) {
                    SpringBeanUtil.registerBean(clazzName, clazz);
                    springBeanNames.add(clazzName);
                } else if (hasSpringAnnotation(clazz)) {
                    SpringBeanUtil.registerBean(clazzName, clazz);
                    springBeanNames.add(clazzName);
                }
            }

            //处理添加了@configuration注解的类，处理添加了@Bean的方法
            List<Class> configurationclasses = classLoader.getConfigurationClasses();
            if (CollectionUtils.isNotEmpty(configurationclasses)) {
                for (Class clazz : configurationclasses) {
                    handleConfigurationBeanAnnotateMethod(clazz);
                }
            }
        }
        //成功记录加载的jar包更新时间戳
        this.loadTimeStamp = file.getAbsoluteFile().lastModified();
        //加载完成
        this.state = LoadState.LOADED;
    }


    /**
     * 加载新增脚本jar包
     *
     * @param file
     * @param componentIds
     * @param jarName
     * @throws Exception
     */
    public void loadGapJar(File file, List<Long> componentIds, String jarName) throws Exception {
        this.state = LoadState.LOADING;
//加载jar包中的所有文件
        try (JarFile jarFile = new JarFile(file.getAbsolutePath())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().equals("dutychain.txt")) {
                    log.info("load gap dutyChain.txt {}", jarEntry.getName());
                    loadComponentToBean(jarFile, jarEntry, componentIds, jarName);
                }
            }
        }
        //成功记录加载的iar包更新时间戳
        this.loadTimeStamp = file.getAbsoluteFile().lastModified();
        //如载完成
        this.state = LoadState.LOADED;

    }

    private void loadComponentToBean(JarFile jarFile, JarEntry jarEntry, List<Long> componentIds, String jarName) {
        InputStream input = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            input = jarFile.getInputStream(jarEntry);
            in = new InputStreamReader(input);
            br = new BufferedReader(in);
            String tempStr;
            StringBuilder stringBuilder = new StringBuilder();
            while ((tempStr = br.readLine()) != null) {
                stringBuilder.append(tempStr);
            }
            String processStr = stringBuilder.toString();
            if (StringUtils.hasLength(processStr)) {
                for (Long componentId : componentIds) {
                    String beanKey = componentId + DUTY_CHAIN;
                    boolean contains = SpringBeanUtil.contains(beanKey);
                    if (contains) {
//如果存在则删除，更新最新配置
                        SpringBeanUtil.destroySingleton(beanKey);
                        log.info("卸载组件出容器:key={}，jarName={}", beanKey, jarName);
                    }
//组件配置的ProcessorHandler
                    List<String> processList = Arrays.asList(processStr.split(";"));
//注册bean到容器
                    SpringBeanUtil.registerSingleton(beanKey, processList);
                    log.info("加载组件到容器:key={},jarName={},value={},", beanKey, jarName, processList);
                }
            } else {
                log.debug("component ids " + componentIds + ": dutychain.txt配置为空");
            }

            }catch(FileNotFoundException e){
            log.error("component ids " + componentIds.toString() + ": 未找到dutychain.txt配置文件", e);
                throw new BussinessException("component ids " + componentIds.toString() + ":未找到processorHandler.txt配置文件");
            }catch(Exception e){
                log.error("component ids" + componentIds.toString() + ": 生成组件责任链异常", e);
                throw new BussinessException("component ids " + componentIds.toString() + ".生成组件责任链异");
            }finally{
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    log.error("component ids " + componentIds.toString() + "生成组件责任链关闭流异常", e);
                }
        }
    }


    /**
     * 卸载jar包
     *
     * @param file
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void unloadJar(File file) throws IOException, NoSuchFieldException, IllegalAccessException {
        //从spring容器中卸载bean对象
        for (String beanName : springBeanNames) {
            try {
                Object bean = SpringBeanUtil.getBean(beanName);
                if (notBeanDefinitionList.contains(beanName)) {
                    SpringBeanUtil.destroySingleton(beanName);
                } else {
                    SpringBeanUtil.destoryBean(beanName, bean);
                }
            } catch (Exception e) {
                log.error("卸载jar包出错:{}", beanName);
            }
        }
        //从spring容器中卸载bean定义
        Map<String, RootBeanDefinition> rootBeanDefinitionMap = SpringBeanUtil.getRootBeanDefinitionMap();
        for (String beanName : springBeanNames) {
            if (!notBeanDefinitionList.contains(beanName)) {
                SpringBeanUtil.removeBean(beanName);
            }
            rootBeanDefinitionMap.remove(beanName);
        }

        springBeanNames.clear();
        classLoader.unloadClass();
        classLoader.close();
        this.loadTimeStamp = null;
        this.state = LoadState.WAIT_LOAD;
    }


    /**
     * 判断是否有注解@P1uginon，即类是否为组件
     * @return
     */
    private boolean isPluginBean(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isInterface()) {
            return false;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        if (clazz.getAnnotation(PluginOn.class) != null) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要重新加载
     *
     * @param timestamp
     * @return
     */
    public boolean isReload(long timestamp) {
        //jar包文件修改时间不变，说明jar未更新，不用加载
        return !Objects.equals(loadTimeStamp, timestamp);
    }


    /**
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return this.state == LoadState.LOADING;
    }


    @Override
    public void close() throws IOException {
        classLoader.close();
    }


    /**
     * 判断类是否为spring注解，注入spring容器，单利模式
     *
     * @return
     */
    private boolean hasSpringAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isInterface()) {
            return false;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        return clazz.getAnnotation(Component.class) != null ||
                clazz.getAnnotation(Repository.class) != null ||
                clazz.getAnnotation(Service.class) != null ||
                clazz.getAnnotation(Controller.class) != null ||
                clazz.getAnnotation(RestController.class) != null ||
                clazz.getAnnotation(ConfigurationProperties.class) != null ||
                clazz.getAnnotation(Configuration.class) != null;
    }


    /**
     * 处理添加@configuration注解的配置类
     */

    private void handleConfigurationBeanAnnotateMethod(Class clazz) throws InvocationTargetException, IllegalAccessException {
        Map<Method, Bean> annotateMethods = null;
        try {
            annotateMethods = MethodIntrospector.selectMethods(clazz,
                    new MethodIntrospector.MetadataLookup<Bean>() {
                        @Override
                        public Bean inspect(Method method) {
                            return AnnotatedElementUtils.findMergedAnnotation(method, Bean.class);
                        }
                    });
        } catch (Exception e) {
        }

        for (Map.Entry<Method, Bean> methodBeanEntity : annotateMethods.entrySet()) {
            Method executeMethod = methodBeanEntity.getKey();
            executeMethod.setAccessible(true);
            Object configBean = SpringBeanUtil.getBean(clazz.getName());
            //获取bean name
            String methodName = executeMethod.getName();
            String[] beanNames = methodBeanEntity.getValue().name();
            if (beanNames != null && beanNames.length > 0) {
                methodName = beanNames[0];
            }
            if (SpringBeanUtil.contains(methodName)) {
                SpringBeanUtil.destroySingleton(methodName);
            }

            Object object = executeMethod.invoke(configBean, null);
            SpringBeanUtil.registerSingleton(methodName, object);
            springBeanNames.add(methodName);
            notBeanDefinitionList.add(methodName);
        }
    }

    /**
     * 处理类中添加@Autowired注解的字段 把有@Autowired注解的字段bean注入springboot容器中
     */
    protected void doAutowiredField(Class clazz) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (declaredFields == null || declaredFields.length == 0) {
                return;
            }
            //遍历字段，处理有@Autowired注解的字段值注入
            for (Field declaredField : declaredFields) {
                if (!declaredField.isAnnotationPresent(Autowired.class) &&
                        !declaredField.isAnnotationPresent(Resource.class)) {
                    continue;
                }

                //获取字段类型是否为接口
                boolean isInterface = declaredField.getType().isInterface();
                if (!isInterface) {
                    //获取字段类型
                    Class fieldClazz = declaredField.getType();
                    String beanName = declaredField.getName();
                    Qualifier annotation = declaredField.getAnnotation(Qualifier.class);
                    if (annotation != null) {
                        beanName = annotation.value();
                    }
                    if (!SpringBeanUtil.contains(beanName)) {
                        SpringBeanUtil.registerBean(beanName, fieldClazz);
                        springBeanNames.add(beanName);
                    }
                }
            }

        } catch (Exception e) {
            log.error("doAutowiredrield class getDeclaredrields error. {}", e.getMessage(), e);
        }
    }