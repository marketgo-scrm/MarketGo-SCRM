package com.easy.marketgo.core.redis.impl;

import cn.hutool.core.util.ArrayUtil;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.redis.lock.RedisLock;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 15:02
 * Describe:
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    /**
     * 默认缓存时间 (分钟)
     */
    public static final int REDIS_CACHE_TIME = 3;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Set<String> distinctRandomMembers(String key, Long size) {
        try {
            return stringRedisTemplate.opsForSet().distinctRandomMembers(key, size);
        } catch (Exception e) {
            log.error("set集合查找不同的随机元素失败, key={}", key, e);
        }
        return Sets.newHashSet();
    }

    @Override
    public List<String> popMembers(String key, Long size) {
        try {
            return stringRedisTemplate.opsForSet().pop(key, size);
        } catch (Exception e) {
            log.error("set集合弹出元素失败, key={}", key, e);
        }
        return Lists.newArrayList();
    }


    /**
     * set方法
     */
    @Override
    public boolean set(String key, String value, Long seconds) {
        boolean flag = false;
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            //expire <= 0 表示无限期, 不设置
            if (seconds > 0) {
                stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
            flag = true;
        } catch (Exception e) {
            log.error("设置缓存失败, key={},value={}", key, value, e);
        }
        return flag;
    }

    @Override
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("设置缓存失败, key={}", key, e);
        }
        return "";
    }

    @Override
    public boolean setObject(String key, Object value, Long seconds) {
        boolean flag = false;
        try {
            flag = set(key, objectMapper.writeValueAsString(value), seconds);
        } catch (Exception e) {
            log.error("设置缓存失败, key={},value={}", key, value, e);
        }
        return flag;
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            String val = String.valueOf(stringRedisTemplate.opsForValue().get(key));
            return objectMapper.readValue(val, clazz);
        } catch (Exception e) {
            log.error("获取缓存失败, key={}", key, e);
        }
        return null;
    }

    /**
     * 判断key是否存在
     */
    @Override
    public boolean exists(String key) {
        try {
            /**
             * 需要先关闭事务 否则在某些Key有事务的场景下会出现 NullPointerException 异常
             *
             * @see {https://www.oschina.net/question/736062_2239693}
             */
            stringRedisTemplate.setEnableTransactionSupport(false);
            return this.stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断缓存是否存在失败, key={}", key, e);
        } finally {
            RedisConnectionUtils.unbindConnection(stringRedisTemplate.getConnectionFactory());
        }
        return false;
    }

    /**
     * 删除
     */
    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long incrBy(String key, Long delta) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        return operations.increment(key, delta);
    }

    @Override
    public Long decrBy(String key, Long delta) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        return operations.increment(key, 0 - delta);
    }

    /**
     * 从redis中获取map
     */
    @Override
    public Map<String, String> hmget(String key) {
        try {
            HashOperations<String, String, String> hash = stringRedisTemplate.opsForHash();
            return hash.entries(key);
        } catch (Exception e) {
            log.error("获取缓存map失败, key={},map={}", key, e);
        }
        return Maps.newHashMap();
    }

    @Override
    public Set<String> hmKeys(String key) {
        try {
            HashOperations<String, String, String> hash = stringRedisTemplate.opsForHash();
            return hash.keys(key);
        } catch (Exception e) {
            log.error("获取缓存key失败, key={},map={}", key, e);
        } finally {
            RedisConnectionUtils.unbindConnection(stringRedisTemplate.getConnectionFactory());
        }
        return Sets.newHashSet();
    }

    @Override
    public Object hmget(String key, String field) {
        try {
            HashOperations<String, String, Object> hash = stringRedisTemplate.opsForHash();
            return hash.get(key, field);
        } catch (Exception e) {
            log.error("获取单个map中的key对应的value缓存失败, key={},map={}", key, e);
        }
        return null;
    }

    @Override
    public String hmgetStrValue(String key, String field) {
        try {
            HashOperations<String, String, String> hash = stringRedisTemplate.opsForHash();
            return hash.get(key, field);
        } catch (Exception e) {
            log.error("获取缓存失败, key={},map={}", key, e);
        }
        return null;
    }

    @Override
    public List<String> multiGetStrValue(String key, List<String> fields) {
        try {
            HashOperations<String, String, String> hash = stringRedisTemplate.opsForHash();
            return hash.multiGet(key, fields);
        } catch (Exception e) {
            log.error("获取缓存失败, key={},map={}", key, e);
        }
        return Collections.emptyList();
    }


    /**
     * 将map存入redis，并设置时效
     */
    @Override
    public void hmset(String key, Map<String, String> map, long timeout) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            if (timeout > 0) {
                stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("设置缓存失败, key={},map={}", key, map, e);
        }
    }

    @Override
    public void hmset(String key, Map<String, String> map, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            if (timeout > 0) {
                stringRedisTemplate.expire(key, timeout, unit);
            }
        } catch (Exception e) {
            log.error("设置缓存失败, key={},map={}", key, map, e);
        }
    }

    @Override
    public void hmset(String key, String field, String value) {
        try {
            stringRedisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("设置缓存失败, key={},field={},value={}", key, field, value, e);
        }
    }

    @Override
    public long hmsize(String key) {
        try {
            return stringRedisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            log.error("获取缓存行数失败, key={}", key, e);
        }
        return 0L;
    }

    @Override
    public void hdel(String key, String field) {
        try {
            stringRedisTemplate.opsForHash().delete(key, field);
        } catch (Exception e) {
            log.error("hdel失败, key={},field={} ", key, field, e);
        }
    }

    @Override
    public List<String> hkeys(String key) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            return Lists.newArrayList(hashOperations.keys(key));
        } catch (Exception e) {
            log.error("设置缓存失败, key={}", key, e);
        }
        return Lists.newArrayList();
    }

    @Override
    public Boolean hexists(String key, String hashKey) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            return hashOperations.hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("查询hash key是否存在失败, key={}, hashKey={}", key, hashKey, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public void hincrby(String key, String hashKey, long inr) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            hashOperations.increment(key, hashKey, inr);
        } catch (Exception e) {
            log.error("自增 hash key - value inr, key={}, hashKey={}", key, hashKey, e);
        }
    }

    @Override
    public void sadd(String key, String... values) {

        if (ArrayUtil.isEmpty(values)) {
            return;
        }
        try {
            stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("set集合设置缓存失败, key={},values={}", key, values, e);
        }
    }

    @Override
    public String spop(String key) {
        try {
            return stringRedisTemplate.opsForSet().pop(key);
        } catch (Exception e) {
            log.error("set集合获取缓存失败, key={},values={}", key, e);
        }
        return null;
    }

    @Override
    public void srem(String key, String value) {
        try {
            stringRedisTemplate.opsForSet().remove(key, value);
        } catch (Exception e) {
            log.error("set集合删除元素失败, key={},value={}", key, value, e);
        }
    }

    @Override
    public Set<String> smembers(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("set集合判断元素是否在Set集合中失败, key={}", key, e);
        }
        return Sets.newHashSet();
    }

    @Override
    public Long sSize(String key) {
        try {
            return stringRedisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("set集合计算setSize失败 key={}", key, e);
        }
        return 0L;
    }


    @Override
    public Cursor<String> sscan(String key, ScanOptions options) {
        try {
            return stringRedisTemplate.opsForSet().scan(key, options);
        } catch (Exception e) {
            log.error("set集合查找元素失败, key={}", key, e);
        }
        return null;
    }

    @Override
    public List<String> cursorPatternKeys(String patternKey) {
        List<String> result = Lists.newArrayList();

        try {
            ScanOptions options = ScanOptions.scanOptions()
                    //这里指定每次扫描key的数量
                    .count(10000)
                    .match(patternKey).build();

            RedisSerializer<String> redisSerializer = (RedisSerializer<String>) stringRedisTemplate.getKeySerializer();
            Cursor<String> cursor = stringRedisTemplate
                    .executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                            redisSerializer::deserialize));
            while (Objects.nonNull(cursor) && cursor.hasNext()) {
                Object next = cursor.next();
                if (Objects.nonNull(next)) {
                    result.add(next.toString());
                }
            }
            //切记这里一定要关闭，否则会耗尽连接数。
            if (Objects.nonNull(cursor) && !cursor.isClosed()) {
                cursor.close();
            }

        } catch (Exception e) {
            log.error("查找匹配集合失败, key={}", patternKey, e);
        }
        return result;

    }

    @Override
    public void deleteKeys(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    @Override
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    @Override
    public Cursor<Map.Entry<String, String>> hscan(String key, ScanOptions options) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            return hashOperations.scan(key, options);
        } catch (Exception e) {
            log.error("set集合查找元素失败, key={}", key, e);
        }
        return null;
    }

    @Override
    public boolean sismember(String key, String value) {
        try {
            Boolean bool = stringRedisTemplate.opsForSet().isMember(key, value);
            if (bool != null) {
                return bool;
            }
            return false;
        } catch (Exception e) {
            log.error("set集合删除元素失败, key={},value={}", key, value, e);
        }
        return false;
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    @Override
    public void expire(String key, long time, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    @Override
    public void expireAt(String key, Date date) {
        stringRedisTemplate.expireAt(key, date);
    }

    @Override
    public long getExpire(String key, TimeUnit timeUnit) {
        Long expire = this.stringRedisTemplate.getExpire(key, timeUnit);
        if (Objects.isNull(expire)) {
            return -1;
        }
        return expire;
    }

    @Override
    public long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public <T> List<T> getDataList(Class<T> clazz, String key, Supplier<List<T>> supplier) {

        return getDataList(clazz, key, REDIS_CACHE_TIME, TimeUnit.MINUTES, supplier);
    }

    @Override
    public <T> T getData(Class<T> clazz, String key, Supplier<T> supplier) {

        return getData(clazz, key, REDIS_CACHE_TIME, TimeUnit.MINUTES, supplier);
    }

    @Override
    public <T> T getData(Class<T> clazz, String key, long timeout, TimeUnit timeUnit, Supplier<T> supplier) {

        List<T> list = getDataList(clazz, key, timeout, timeUnit, () -> Lists.newArrayList(supplier.get()));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }


    @Override
    public <T> List<T> getDataList(Class<T> clazz, String key, long timeout, TimeUnit timeUnit, Supplier<List<T>> supplier) {

        String val = String.valueOf(stringRedisTemplate.opsForValue().get(key));
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        List<T> resultList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(val)) {
            try {
                resultList = objectMapper.readValue(val, javaType);
                if (CollectionUtils.isNotEmpty(resultList)) {
                    if (log.isDebugEnabled()) {
                        log.debug("|prefix=查询 List<{}> 集合: 读取到redis缓存数据|key={}|", clazz.getName(), key);
                    }
                    return resultList;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }

        RedisLock lock = new RedisLock(stringRedisTemplate, key, 30, TimeUnit.SECONDS);

        boolean hasLocked = false;
        try {
            hasLocked = lock.tryLock(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取操作锁失败, key={}", key, e);
        }

        val = String.valueOf(stringRedisTemplate.opsForValue().get(key));
        if (val != null && !"null".equals(val)) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("|prefix=查询 List<{}> 集合: 进入redis锁之后, 读取到redis缓存数据|key={}|", clazz.getName(), key);
                }
                resultList = objectMapper.readValue(val, javaType);
            } catch (IOException e) {
                log.error("查询{}集合缓存出错！", clazz.getName(), e);
            } finally {
                // 释放锁
                if (hasLocked) {
                    lock.unlock();
                }
            }
            return resultList;
        }
        if (log.isDebugEnabled()) {
            log.debug("|prefix=当前{}数据: 未读取到redis缓存数据, 从数据库中读取数据并缓存到redis, 缓存超时时间 {}{}|key={}", clazz.getName(), timeout, timeUnit, key);
        }
        try {
            resultList = supplier.get();
            String json = objectMapper.writeValueAsString(resultList);
            stringRedisTemplate.opsForValue().set(key, json);
            stringRedisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("查询{}集合缓存出错！", clazz.getName(), e);
        } finally {
            // 释放锁
            if (hasLocked) {
                lock.unlock();
            }
        }
        return resultList;
    }
}
