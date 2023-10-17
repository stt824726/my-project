package com.stt.core.util.classUtil;


import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 注解工具类
 */
@Slf4j
public class AnnotationUtils {

    public static List<Field> getAllFile(Class clazz) {
        List<Field> fieldList = new LinkedList<>();
        getAllFile(clazz, fieldList);
        return fieldList;
    }

    public static void getAllFile(Class clazz, List<Field> fieldList) {
        if (clazz == Object.class) {
            return;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields != null && declaredFields.length > 0) {
            for (Field field : declaredFields) {
                fieldList.add(field);
            }
        }
        Class superclass = clazz.getSuperclass();
        getAllFile(superclass, fieldList);
    }


    public static <T extends Annotation> T getFieldAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return (T) field.getDeclaredAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getClassAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        return (T) clazz.getDeclaredAnnotation(annotationClass);
    }

    public static Object getFieldValue(Field field, Object object) {
        String fieldName = field.getName();
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Method method = object.getClass().getMethod(methodName);
            if (method == null) {
                log.warn("method name:{} not found in class:{}", methodName, field.getType());
                return null;
            }
            return method.invoke(object);
        } catch (NoSuchMethodException e) {
            log.error("{}", e);
        } catch (IllegalAccessException e) {
            log.error("{}", e);
        } catch (InvocationTargetException e) {
            log.error("{}", e);
        }
        return null;
    }
}
