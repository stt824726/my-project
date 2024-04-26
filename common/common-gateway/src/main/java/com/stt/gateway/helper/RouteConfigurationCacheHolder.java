package com.stt.gateway.helper;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.google.common.cache.CacheBuilder;
import com.stt.core.util.common.LocalCacheUtil;
import com.stt.gateway.constant.RouteDefinitionVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 路由缓存类
 * @author: shaott
 * @create: 2024-04-24 11:18
 * @Version 1.0
 **/
public class RouteConfigurationCacheHolder {

    private static Cache<String, RouteDefinitionVo> cache = CacheUtil.newLFUCache(50);

    /**
     * 获取缓存的全部对象
     * @return routeList
     */
    public static List<RouteDefinitionVo> getRouteList() {
        List<RouteDefinitionVo> routeList = new ArrayList<>();
        cache.forEach(route -> routeList.add(route));
        return routeList;
    }

    /**
     * 更新缓存
     * @param routeList 缓存列表
     */
    public static void setRouteList(List<RouteDefinitionVo> routeList) {
        routeList.forEach(route -> cache.put(route.getId(), route));
    }

    /**
     * 清空缓存
     */
    public static void removeRouteList() {
        cache.clear();
    }

}
