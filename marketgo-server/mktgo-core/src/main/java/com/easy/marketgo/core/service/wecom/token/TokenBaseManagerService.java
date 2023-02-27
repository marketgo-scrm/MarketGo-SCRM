package com.easy.marketgo.core.service.wecom.token;

import com.easy.marketgo.core.model.wecom.QueryTokenBaseRequest;
import com.easy.marketgo.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 10:45
 * Describe:
 */
@Slf4j
@Service
public abstract class TokenBaseManagerService {

    protected Lock reentrantLock = new ReentrantLock();


    @Resource
    protected RedisService redisService;

    protected String getTokenFromCache(String key) {
        return redisService.get(key);
    }

    public void setTokenToCache(String key, String token, long seconds) {
        try {
            redisService.set(key, token, seconds);
        } catch (Exception e) {
            log.error("failed to set key to redis. key={}, values={}, seconds={}", key, token, seconds, e);
        }
    }

    protected void lock() {
        try {
            reentrantLock.lock();
        } catch (Exception e) {
            reentrantLock.unlock();
        }
    }

    protected void unlock() {
        reentrantLock.unlock();
    }

    protected abstract String getTokenFromWeCom(QueryTokenBaseRequest request);
}
