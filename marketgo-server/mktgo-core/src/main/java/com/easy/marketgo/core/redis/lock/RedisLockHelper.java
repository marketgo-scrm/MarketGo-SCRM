package com.easy.marketgo.core.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-31 14:56
 * Describe:
 */
public class RedisLockHelper {
    protected final static Logger logger = LoggerFactory.getLogger(RedisLockHelper.class);

    public static <T> T lockAndSupply(RedisLock lock,
                                      long lockTime,
                                      TimeUnit lockTimeUnit,
                                      Supplier<T> supplier
    ) {
        boolean hasLocked = false;

        try {
            hasLocked = lock.tryLock(lockTime, lockTimeUnit);
        } catch (Exception e) {
            logger.error("获取操作锁失败, key=" + lock.getKey(), e);
        }

        try {
            // double get check
            return supplier.get();
        } finally {
            // 释放锁
            if (hasLocked) {
                lock.unlock();
            }
        }
    }
}
