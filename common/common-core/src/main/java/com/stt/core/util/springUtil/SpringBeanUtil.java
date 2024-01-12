package com.stt.core.util.springUtil;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * bean帮助工具类
 */
@Slf4j
public class SpringBeanUtil implements ApplicationContextAware {

    public static ConfigurableApplicationContext context;
    public static DefaultListableBeanFactory beanFactory;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext)applicationContext;
        beanFactory = (DefaultListableBeanFactory)context.getBeanFactory();
    }


    public static ConfigurableApplicationContext getContext(){
        return context;
    }


    /**
     * 注册bean到spring ioc容器中
     * @param beanName
     * @param clazz
     */
    public static void registerBean(String beanName,Class clazz){
        registerBean(beanName,clazz, ConfigurableBeanFactory.SCOPE_PROTOTYPE);
    }


    /**
     * 注册bean到spring ioc容器中。类型为单例
     * @param beanName
     * @param obj
     */
    public static void registerSingleton(String beanName,Object obj){
        beanFactory.registerSingleton(beanName,obj);
    }


    /**
     * 从容器中获取bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return beanFactory.getBean(clazz);
    }


    /**
     * 注册指定controller到spring ioc容器中
     * @param beanName
     * @param clazz
     * @throws Exception
     */
    public static void registerController(String beanName,Class clazz) throws Exception{
        //先注册bean
        registerBean(beanName,clazz,null);
        //注册controller到RequestMappingHandlerMapping
        RequestMappingHandlerMapping handlerMapping = SpringBeanUtil.getBean(RequestMappingHandlerMapping.class);
        if(handlerMapping != null){
            Method method = handlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod("detectHandlerMethods",Object.class);
            method.setAccessible(true);
            method.invoke(handlerMapping,beanName);
        }
    }


    /**
     * 从spring容器中删除bean
     * @param beanName
     */
    public static void removeBeanDefinition(String beanName){
        Object bean = beanFactory.getBean(beanName);
        if(bean == null){
            return;
        }
        beanFactory.removeBeanDefinition(beanName);
    }


    /**
     * 销毁单例模式的bean
     * @param beanName
     */
    public static void destroySingleton(String beanName){
        beanFactory.destroySingleton(beanName);
    }


    /**
     * 判断bean是否存在
     * @param beanName
     * @return
     */
    public static boolean contains(String beanName){
        return context.containsBean(beanName);
    }


    /**
     * 从容器中获取bean
     * @param beanName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName,Class<T> clazz){
        return beanFactory.getBean(beanName,clazz);
    }


    /**
     * 获取指定注解的beanMap
     * @param clazz
     * @return
     */
    public static Map<String,Object> getBeanMap(Class<? extends Annotation> clazz){
        return context.getBeansWithAnnotation(clazz);
    }


    /**
     * 判断是否controller
     * @param clazz
     * @return
     */
    public static boolean isController(Class<?> clazz){
        if(!isTrueClass(clazz)){
            return false;
        }
        if(clazz.getAnnotation(Controller.class) != null || clazz.getAnnotation(RestController.class) != null){
            return true;
        }
        return false;
    }


    /**
     * 从spring中移除controller
     * @param beanName
     */
    public static void removeControllerBean(String beanName){
        Object controller = beanFactory.getBean(beanName);
        if(controller == null){
            return;
        }
        if(isController(controller.getClass())){
            removeController(beanName);
        }else{
            beanFactory.removeBeanDefinition(beanName);
        }
    }


    /**
     * 销毁单例的bean
     * @param beanName
     */
    public static void destorySingleton(String beanName){
        beanFactory.destroySingleton(beanName);
    }


    /**
     * 从spring中删除controller
     * @param beanName
     */
    protected static void removeController(String beanName){
        beanFactory.removeBeanDefinition(beanName);

        final RequestMappingHandlerMapping handlerMapping = SpringBeanUtil.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
        if(handlerMapping == null){
            return;
        }
        Object controller = beanFactory.getBean(beanName);
        if(controller == null){
            return;
        }

        final Class<?> targetClazz = controller.getClass();
        ReflectionUtils.doWithMethods(targetClazz,method -> {
            Method specificMethod = ClassUtils.getMostSpecificMethod(method,targetClazz);
            try{
                Method createMappingMethod = RequestMappingHandlerMapping.class.getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                createMappingMethod.setAccessible(true);
                RequestMappingInfo requestMappingInfo = (RequestMappingInfo)createMappingMethod.invoke(handlerMapping,specificMethod,targetClazz);
                if(requestMappingInfo != null){
                    handlerMapping.unregisterMapping(requestMappingInfo);
                }
            }catch (Exception e){
                log.error("从spring中删除controller bean时报错:{}",beanName,e);
            }
        },ReflectionUtils.USER_DECLARED_METHODS);
    }


    /**
     * 判断是否为注册类
     * @param clazz
     * @return
     */
    public static boolean isConfiguration(Class<?> clazz){
        if(!isTrueClass(clazz)){
            return false;
        }
        if(clazz.getAnnotation(Configuration.class) != null){
            return true;
        }
        return false;
    }


    /**
     * 判断类是否不是接口或者抽象类
     * @param clazz
     * @return
     */
    public static boolean isTrueClass(Class<?> clazz){
        if(clazz == null){
            return false;
        }
        if(clazz.isInterface()){
            return false;
        }
        if(Modifier.isAbstract(clazz.getModifiers())){
            return false;
        }
        return true;
    }


    /**
     * 注册指定bean到spring容器中
     * @param beanName
     * @param clazz
     * @param scope
     */
    private static void registerBean(String beanName,Class clazz,String scope){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setScope(scope);
        beanFactory.registerBeanDefinition(beanName,beanDefinition);
        //允许注入和反向注入
        beanFactory.autowireBean(clazz);
        beanFactory.initializeBean(clazz,beanName);
    }
}
