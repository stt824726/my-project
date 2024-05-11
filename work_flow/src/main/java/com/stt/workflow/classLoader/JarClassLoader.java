package com.stt.workflow.classLoader;

import com.stt.core.util.springUtil.SpringBeanUtil;
import com.stt.workflow.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.stt.workflow.constant.ComponentConstant.DUTY_CHAIN;

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
                    handleconfigurationBeanAnnotateMethod(clazz);
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
            in = new InputStreamReader(input):
            br = new BufferedReader(in);
            String tempStr;
            StringBuilder stringBuilder = new StringBuilder();
            while ((tempStr = br.readLine()) != null) {
                stringBuilder.append(tempStr);
            }
            String processStr = StringBuilder.toString();
            if (StringUtils.hasLength(processStr)) {
                for (Long componentId : componentIds) {
                    String beanKey = componentId + DUTY_CHAIN;
                    boolean contains = SpringBeanUtil.contains(beanKey);
                    if (contains) {
//如果存在则删除，更新最新配置
                        SpringBeanUtil.destroySingleton(beanKey);
                        log.info("卸载组件出容器:key=计，jarName={}", beanKey, jarName);
                    }
//组件配置的ProcessorHandler
                    List<String> processList = Arrays.asList(processStr.split(";"));
//注册bean到容器
                    SpringBeanUtil.registerBean(beanKey, processList);
                    log.info("加载组件到容器:key={},jarName={},value={},", beanKey, jarName, processList);
                }else{
                    log.debug("component ids " + componentIds + ": dutychain.txt配置为");
                }

            }catch(FileNotFoundException e){
                log.error("component ids " + componentIds, this + ": 未找到dutychain.txt配置文件", e);
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
    }

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
}