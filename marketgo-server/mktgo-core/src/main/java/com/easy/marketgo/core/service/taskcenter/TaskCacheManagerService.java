package com.easy.marketgo.core.service.taskcenter;

import cn.hutool.core.codec.Base64;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/28/22 5:58 PM
 * Describe:
 */
@Slf4j
@Service
public class TaskCacheManagerService {

    private final static String CACHE_CONTENT_KEY_NAME = "content_%s";

    private final static long CACHE_SAVE_TIME = 30 * 24 * 60 * 60;

    private final static long CACHE_CONTENT_SAVE_TIME = 3 * 24 * 60 * 60;

    @Autowired
    private RedisService redisService;

    public void setCacheContent(String uuid, String content) {
        redisService.set(String.format(CACHE_CONTENT_KEY_NAME, uuid), content, CACHE_CONTENT_SAVE_TIME);
    }

    public String getCacheContent(String uuid) {
        String content = redisService.get(String.format(CACHE_CONTENT_KEY_NAME, uuid));
        return (StringUtils.isNotBlank(content)) ? content : null;
    }

    public void delCacheContent(String uuid) {
        redisService.delete(String.format(CACHE_CONTENT_KEY_NAME, uuid));
    }


    public void setCustomerCache(String memberId, String uuid, String taskUuid, String externalUserId,
                                 String externalUserName) {
        String key = String.format("%s##%s##%s##%s##%s", memberId, uuid, taskUuid,
                externalUserId, Base64.encode(externalUserName));
        log.info("save external user message for single task center to cache . key={}", key);
        redisService.set(key, WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), CACHE_SAVE_TIME);
    }

    public void setMemberCache(String memberId, String uuid, String taskUuid) {
        redisService.set(String.format("%s##%s##%s", memberId, uuid, taskUuid),
                WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), CACHE_SAVE_TIME);
    }

    public void delCustomerCache(String memberId, String uuid, String taskUuid) {
        String key = String.format("%s##%s##%s", memberId, uuid, taskUuid);
        List<String> keys = redisService.cursorPatternKeys(key);
        log.info("save external user message for single task center to cache . keys={}", keys);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisService.deleteKeys(keys);
        }
    }

    public void delMemberCache(String memberId, String uuid, String taskUuid) {
        redisService.delete(String.format("%s##%s##%s", memberId, uuid, taskUuid));
    }

}
