package com.stt.gateway.helper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @description: 路由缓存类
 * @author: shaott
 * @create: 2024-04-24 11:18
 * @Version 1.0
 **/
public class RouteConfigurationCacheHolder {
    // 缓存最大数量
    private static final int MAX_CACHE_SIZE = 1000;

    private static final Cache<String, String> CACHE ;

    static {
        CACHE = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE)
                .build();
    }


    /**
     * 将键值对存入缓存
     */
    public static void put(String key, String value) {
        CACHE.put(key, value);
    }

    /**
     * 从缓存中获取值，若不存在则返回null
     */
    public static String get(String key) {
        return CACHE.getIfPresent(key);
    }

    /**
     * 清空缓存
     */
    public static void clear(){
        CACHE.invalidateAll();
    }
}
