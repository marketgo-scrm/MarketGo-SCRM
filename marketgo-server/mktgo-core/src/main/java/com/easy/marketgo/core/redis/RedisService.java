package com.easy.marketgo.core.redis;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 14:59
 * Describe:
 */
public interface RedisService {

    boolean set(String key, String value, Long seconds);

    String get(String key);

    boolean setObject(String key, Object value, Long seconds);

    <T> T getObject(String key, Class<T> clazz);

    boolean exists(String key);

    void delete(String key);

    Long incrBy(String key, Long delta);

    Long decrBy(String key, Long delta);


    Map<String, String> hmget(String key);

    Set<String> hmKeys(String key);

    Object hmget(String key, String field);

    String hmgetStrValue(String key, String field);

    List<String> multiGetStrValue(String key, List<String> fields);

    void hmset(String key, Map<String, String> map, long timeout);

    void hmset(String key, Map<String, String> map, long timeout, TimeUnit unit);

    void hmset(String key, String field, String value);

    long hmsize(String key);

    Cursor<Map.Entry<String, String>> hscan(String key, ScanOptions options);

    void hdel(String key, String field);

    List<String> hkeys(String key);

    Boolean hexists(String key, String hashKey);

    void hincrby(String key, String hashKey, long inr);

    void sadd(String key, String... values);

    void srem(String key, String value);

    String spop(String key);

    boolean sismember(String key, String value);

    Set<String> smembers(String key);

    Set<String> distinctRandomMembers(String key, Long size);

    List<String> popMembers(String key, Long size);

    Long sSize(String key);

    Cursor<String> sscan(String key, ScanOptions options);

    void expire(String key, long time, TimeUnit timeUnit);

    void expireAt(String key, Date date);

    long getExpire(String key, TimeUnit timeUnit);

    long getExpire(String key);

    /**
     * 从缓存去读集合数据
     *
     * @param clazz
     * @param key
     * @param supplier
     * @param <T>
     * @return
     */
    <T> List<T> getDataList(Class<T> clazz, String key, Supplier<List<T>> supplier);

    /**
     * 从缓存去读集合数据
     *
     * @param clazz
     * @param key
     * @param timeout
     * @param timeUnit
     * @param supplier
     * @return
     */
    <T> List<T> getDataList(Class<T> clazz, String key, long timeout, TimeUnit timeUnit, Supplier<List<T>> supplier);


    /**
     * 从缓存去读单条数据
     *
     * @param supplier
     * @param clazz
     * @param key
     * @return
     */
    <T> T getData(Class<T> clazz, String key, Supplier<T> supplier);

    /**
     * 从缓存去读单条数据
     *
     * @param supplier
     * @param clazz
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    <T> T getData(Class<T> clazz, String key, long timeout, TimeUnit timeUnit, Supplier<T> supplier);

    /**
     * 匹配查找相关集合
     * @param patternKey
     * @return
     */
    List<String> cursorPatternKeys(String patternKey);

    /**
     * 批量删除key
     * @param keys
     */
    void deleteKeys(Collection<String> keys);


    /**
     * 根据pattern 获取所有的key
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);
}
