package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.taskcenter.WeComMomentTaskCenterRequest;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 10:19 PM
 * Describe:
 */
@Slf4j
@Component
public class ContentUpdateCacheService {

    private static long CACHE_CONTENT_EXPIRE_TIME = 24 * 60 * 60;

    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @Autowired
    private SendBaseTaskCenterProducer sendBaseTaskCenterProducer;

    public void checkContentExpireIn() {
        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.querytByNotInTaskStatus(Arrays.asList(WeComMassTaskStatus.UNSTART.getValue(),
                        WeComMassTaskStatus.COMPUTE_FAILED.getValue(),
                        WeComMassTaskStatus.SEND_FAILED.getValue(),
                        WeComMassTaskStatus.FINISHED.getValue()));
        if (CollectionUtils.isEmpty(entities)) {
            log.info("not find update content for task center");
            return;
        }

        for (WeComTaskCenterEntity entity : entities) {
            log.info("update content for task center. entity={}", entity);
            String taskUuid = entity.getUuid();
            Long expireTime = taskCacheManagerService.getCacheContentExpireTime(taskUuid);
            if (expireTime == null || expireTime < CACHE_CONTENT_EXPIRE_TIME) {
                updateCacheTaskCenterContent(entity);
            }
        }
    }

    private void updateCacheTaskCenterContent(WeComTaskCenterEntity entity) {
        if (entity.getTaskType().equals(WeComMassTaskTypeEnum.MOMENT.getName())) {
            WeComMomentTaskCenterRequest momentTaskCenterRequest =
                    sendBaseTaskCenterProducer.buildMomentTaskCenterContent(entity);
            taskCacheManagerService.setCacheContent(entity.getUuid(), JsonUtils.toJSONString(momentTaskCenterRequest));
        } else {
            WeComTaskCenterRequest request = sendBaseTaskCenterProducer.buildTaskCenterContent(entity);
            log.info("update content for task center. taskUuid={}， content={}", entity.getUuid(),
                    JsonUtils.toJSONString(request));
            taskCacheManagerService.setCacheContent(entity.getUuid(), JsonUtils.toJSONString(request));
        }
        log.info("finish to update content for task center. taskUuid={}", entity.getUuid());
    }
}
