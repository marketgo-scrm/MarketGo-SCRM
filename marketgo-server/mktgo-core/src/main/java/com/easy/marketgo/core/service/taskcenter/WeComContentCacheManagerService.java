package com.easy.marketgo.core.service.taskcenter;

import com.easy.marketgo.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/28/22 5:58 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComContentCacheManagerService {

    private final static String CACHE_CONTENT_KEY_NAME = "content_%s";

    @Autowired
    private RedisService redisService;

    public void setCacheContent(String uuid, String content) {
        redisService.set(String.format(CACHE_CONTENT_KEY_NAME, uuid), content, 0L);
    }

    public String getCacheContent(String uuid) {
        String content = redisService.get(String.format(CACHE_CONTENT_KEY_NAME, uuid));
        return (StringUtils.isNotBlank(content)) ? content : null;
    }
}
