package com.stt.common.redis.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: shaott
 * @create: 2024-04-26 15:01
 * @Version 1.0
 **/
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> template;

    private  RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate = template;
    }

    public  void removeObject(String key) {
        log.warn("remove : {}", key);
        redisTemplate.delete(key);
    }

    public  void removeLike(String key) {
        log.warn("redis remove like key: {}", key);
        Set<String> keys = redisTemplate.keys(key + ":*");
        redisTemplate.delete(keys);
    }

    public  ValueOperations<String, Object> setObject(String key, Object object, long duration) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        operation.set(key, object, duration, TimeUnit.SECONDS);
        return operation;
    }

    public  ValueOperations<String, Object> setObject(String key, Object object) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        operation.set(key, object);
        return operation;
    }

    public  Object getObject(String key) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }


    public  String getString(String key) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        return operation.get(key) == null? null:operation.get(key).toString();
    }

    public  Object setOperationAdd(String key,Object object) {
        SetOperations<String,Object> setOperations=redisTemplate.opsForSet();
        //向集合中添加一个元素,自动去重
        return setOperations.add(key,object);
    }
    public  Object setOperationGet(String key) {
        SetOperations<String,Object> setOperations=redisTemplate.opsForSet();
        //随机返回集合中的一个元素并且从集合中删除
        return setOperations.pop(key);
    }

    public  Long leftPushAll(String key, Collection<Object> values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        //redis列表操作
        return listOperations.leftPushAll(key, values);
    }

    @SuppressWarnings("unchecked")
    public  Long rightPushAll(String key, Collection values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        //redis列表操作,追加元素
        return listOperations.rightPushAll(key, values);
    }

    public  Long listSize(String key) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        //redis列表长度
        return listOperations.size(key);
    }

    public  List rangeList(String key, long start, long end){
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return listOperations.range(key,start,end);
    }

    public  Boolean expireObject(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public  void pipeAdd(String key, List<Long> values, Long expire) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisConnection stringRedisConn = (RedisConnection) connection;
                values.forEach(x->{
                    stringRedisConn.setCommands().sAdd(key.getBytes(), String.valueOf(x).getBytes());
                });
                return null;
            }
        });
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 添加一个键值对到指定键的hash散列中
     * filed不存在则新增，field存在则修改field对应的值。
     * @param hash
     * @param hashKey
     * @param hashValue
     */
    public  void hashOperationAdd(String hash, Object hashKey, Object hashValue) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(hash, hashKey, hashValue);
    }

    /**
     * 从指定键的hash散列中取出一个指定键的值，并且删除这个键值对
     * @param hash
     * @param hashKey
     * @return
     */
    public  Object hashOperationGetAndDelete(String hash, Object hashKey) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Object object = hashOperations.get(hash, hashKey);
        if (object != null) {
            hashOperations.delete(hash,hashKey);
        }
        return object;
    }

    /**
     * 查询指定的键是否存在
     * @param hash
     * @param hashKey
     * @return
     */
    public  Boolean hasHashKey(String hash, Object hashKey) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.hasKey(hash, hashKey);
    }

    /**
     * 从指定键的hash散列中取出一个指定键的值
     * @param hash
     * @param hashKey
     * @return
     */
    public  Object hashOperationGet(String hash, Object hashKey) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Object object = hashOperations.get(hash, hashKey);
        return object;
    }
}
