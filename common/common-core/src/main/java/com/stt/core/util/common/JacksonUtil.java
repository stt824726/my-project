package com.stt.core.util.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @description:
 * @author: shaott
 * @create: 2024-05-08 17:57
 * @Version 1.0
 **/
@Slf4j
public class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String STAND_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) ;
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) ;
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(STAND_FORMAT));
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JacksonUtil(){

    }

    public static String writeString(Object o) {
        if(Objects.isNull(o)){
            return null;
        }
        try {
            return o instanceof  String ? (String)o : OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("writeString error", e);
            return null;
        }
    }
    public static <T> T writeObject(String s,Class<T> clazz) {
        if(StringUtils.isBlank(s)){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T)s : OBJECT_MAPPER.readValue(s,clazz);
        } catch (Exception e) {
            log.warn("writeObject error", e);
            return null;
        }
    }
}
