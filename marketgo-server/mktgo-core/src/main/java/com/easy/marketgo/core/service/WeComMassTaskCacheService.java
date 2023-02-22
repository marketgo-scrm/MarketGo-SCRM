package com.easy.marketgo.core.service;

import com.easy.marketgo.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/28/22 5:57 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComMassTaskCacheService {

    public final static String CACHE_REMIND_KEY = "mass_task_remind_%s";

    @Autowired
    private RedisService redisService;

    public String getCacheRemindCount(String taskUuid) {
        String content = redisService.get(String.format(CACHE_REMIND_KEY, taskUuid));
        return (StringUtils.isNotBlank(content)) ? content : null;
    }

    public void setCacheContent(String taskUuid, String count, long times) {
        redisService.set(String.format(CACHE_REMIND_KEY, taskUuid), count, times);
    }
}
