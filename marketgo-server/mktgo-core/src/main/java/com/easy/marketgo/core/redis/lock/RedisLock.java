package com.easy.marketgo.core.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-31 14:56
 * Describe:
 */
public class RedisLock implements Lock {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String KEY_PREFIX = "__redis_lock_";

    protected static final String KEY_SUFFIX_TEMP = "_temp";

    private String key;

    private long timeout; // key的过期时间

    private TimeUnit timeUnit; // key的过期时间单位

    private boolean locked = false;

    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param stringRedisTemplate redis操作模板
     * @param name 锁的名称
     * @param timeout key过期时间
     * @param timeUnit key过期时间的单位
     */
    public RedisLock(StringRedisTemplate stringRedisTemplate, String name, long timeout, TimeUnit timeUnit) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.key = KEY_PREFIX + name;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void lock() {

        try {
            this.lockInterruptibly();
        } catch (InterruptedException e) {
            this.logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

        while (!this.tryLock()) {

        }
    }

    @Override
    public boolean tryLock() {

        if (this.stringRedisTemplate == null) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        String expireStr = String.valueOf(currentTimeMillis + this.timeUnit.toMillis(this.timeout) + 1000);

        {
            String tempKey = this.key + KEY_SUFFIX_TEMP;
            Object txResult = this.stringRedisTemplate.execute(new SessionCallback<List<Object>>() {

                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {

                    RedisTemplate redisTemplate = (RedisTemplate) operations;
                    redisTemplate.multi();

                    // 通过 SETEX 和 RENAMENX 模拟实现 SETNXEX
                    redisTemplate.opsForValue().set(tempKey, expireStr, RedisLock.this.timeout, RedisLock.this.timeUnit);   // set没有返回值
                    redisTemplate.renameIfAbsent(tempKey, RedisLock.this.key);
                    redisTemplate.delete(tempKey);

                    // This will contain the results of all ops in the transaction
                    return redisTemplate.exec();
                }
            });
            // 第一个返回结果即为 RENAMENX 的返回结果 （lyr:redis升级2.0.8后size=3改为获取第二个结果）
            if (txResult != null) {
                List<Object> tx = (List<Object>) txResult;
                if (tx.get(1).equals(Boolean.TRUE)) {
                    this.locked = true;
                    return true;
                }
            }
        }

        // 如果未成功创建锁(锁已存在), 判断锁是否已经过期
        Object currentValueStr = this.stringRedisTemplate.opsForValue().get(this.key);
        if (currentValueStr != null && Long.parseLong(currentValueStr.toString()) < currentTimeMillis) {

            Object txResult = this.stringRedisTemplate.execute(new SessionCallback<List<Object>>() {

                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {

                    RedisTemplate redisTemplate = (RedisTemplate) operations;
                    redisTemplate.multi();

                    // 通过 SETEX 和 RENAMENX 模拟实现 SETNXEX
                    redisTemplate.opsForValue().getAndSet(RedisLock.this.key, expireStr);
                    redisTemplate.expire(RedisLock.this.key, RedisLock.this.timeout, RedisLock.this.timeUnit);

                    // This will contain the results of all ops in the transaction
                    return redisTemplate.exec();
                }
            });

            // 第一个返回结果即为 getAndSet 的返回结果
            if (txResult != null) {
                List<Object> tx = (List<Object>) txResult;
                if (tx.size() > 0 && tx.get(0) != null && tx.get(0).equals(currentValueStr.toString())) {
                    this.locked = true;
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {

        long timeout = timeUnit.toMillis(l);// timeout单位毫秒
        while (!this.tryLock()) {
            if (timeout <= 0) {
                return false;
            }
            timeout -= 100;
            synchronized (this) {
                this.wait(100);
            }
        }
        return true;
    }

    @Override
    public void unlock() {

        try {
            if (this.locked) {
                if (this.stringRedisTemplate == null) {
                    throw new IllegalStateException("unlock failed, lock key is " + this.key + ", cause by :connection is null! ");
                }
                this.stringRedisTemplate.delete(this.key);
            }
        } finally {
            this.locked = false;
        }
    }

    @Override
    public Condition newCondition() {

        throw new UnsupportedOperationException();
    }

    public String getKey() {

        return this.key;
    }
}
