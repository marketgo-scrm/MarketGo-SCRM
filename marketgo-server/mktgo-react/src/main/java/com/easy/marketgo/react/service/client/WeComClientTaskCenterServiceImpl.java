package com.easy.marketgo.react.service.client;

import cn.hutool.core.codec.Base64;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.service.taskcenter.SendTaskCenterBaseConsumer;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/3/23 11:33 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComClientTaskCenterServiceImpl implements WeComClientTaskCenterService {

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @Autowired
    private SendTaskCenterBaseConsumer sendTaskCenterBaseConsumer;

    @Override
    public BaseResponse listTaskCenter(List<String> type, List<String> taskTypes, Integer pageNum, Integer pageSize,
                                       String corpId, List<String> statuses, String keyword, String memberId,
                                       List<String> createUserIds, String sortKey, String sortOrder, String startTime
            , String endTime) {
        return null;
    }

    @Override
    public WeComTaskCenterDetailResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid,
                                                              String uuid) {
        log.info("start to query task center detail. corpId={}, memberId={}, taskUuid={}, uuid={}", corpId, memberId,
                taskUuid, uuid);
        String value = taskCacheManagerService.getMemberCache(corpId, memberId, taskUuid, uuid);
        if (StringUtils.isBlank(value)) {
            throw new CommonException(ErrorCodeEnum.ERROR_REACT_TASK_IS_NOT_EXIST);
        }
        log.info("query customer cache message. value={}", value);
        String content = taskCacheManagerService.getCacheContent(taskUuid);
        if (StringUtils.isBlank(content)) {
            throw new CommonException(ErrorCodeEnum.ERROR_REACT_TASK_CONTENT_IS_NOT_EXIST);
        }
        log.info("query content cache message. content={}", content);
        WeComTaskCenterDetailResponse detailClientResponse = JsonUtils.toObject(content,
                WeComTaskCenterDetailResponse.class);

        List<String> keys = taskCacheManagerService.scanCustomerCache(corpId, memberId, taskUuid, uuid);
        if (CollectionUtils.isEmpty(keys)) {
            throw new CommonException(ErrorCodeEnum.ERROR_REACT_TASK_CUSTOMER_LIST_IS_NOT_EXIST);
        }
        List<WeComTaskCenterDetailResponse.ExternalUserMessage> users = new ArrayList<>();
        keys.forEach(item -> {
            WeComTaskCenterDetailResponse.ExternalUserMessage message =
                    new WeComTaskCenterDetailResponse.ExternalUserMessage();
            String[] values =
                    item.replace(TaskCacheManagerService.CACHE_CUSTOMER_REPLACE_KEY, "").split(TaskCacheManagerService.CACHE_KEY_SPLIT_CHARACTER);
            log.info("parse customer key. item={}, values size={}", item, values.length);
            if (values.length >= 6) {
                message.setExternalUserId(values[4]);
                message.setName(Base64.decodeStr(values[5]));
                if (values.length == 7) {
                    message.setAvatar(Base64.decodeStr(values[6]));
                }
            }
            String status = taskCacheManagerService.getCustomerCache(item);
            message.setStatus(status);
            users.add(message);
        });
        detailClientResponse.setUuid(uuid);
        detailClientResponse.setExternalUserId(users);
        log.info("finish to query task center detail. response={}", detailClientResponse);
        return detailClientResponse;
    }

    @Override
    public WeComTaskCenterDetailResponse getTaskCenterContent(String corpId, String memberId, String taskUuid) {
        log.info("query task center cache content. corpId={}, taskUuid={}, memberId={}", corpId, taskUuid, memberId);
        String content = taskCacheManagerService.getCacheContent(taskUuid);
        if (StringUtils.isBlank(content)) {
            throw new CommonException(ErrorCodeEnum.ERROR_REACT_TASK_CONTENT_IS_NOT_EXIST);
        }
        log.info("query content cache message. content={}", content);
        WeComTaskCenterDetailResponse detailClientResponse = JsonUtils.toObject(content,
                WeComTaskCenterDetailResponse.class);
        return detailClientResponse;
    }

    @Override
    public BaseResponse changeTaskCenterMemberStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                     String sendTime, String status) {
        sendTaskCenterBaseConsumer.sendMemberStatusDetail("", corpId, WeComMassTaskTypeEnum.SINGLE,
                uuid, taskUuid, memberId, "", sendTime, WeComMassTaskMemberStatusEnum.valueOf(status), 0, Boolean.TRUE);
        taskCacheManagerService.setMemberCache(corpId, memberId, taskUuid, uuid, status);

        return BaseResponse.success();
    }

    @Override
    public BaseResponse changeTaskCenterExternalUserStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                           String externalUserId, String sentTime, String status) {

        sendTaskCenterBaseConsumer.sendExternalUserStatusDetail("", corpId, null, taskUuid, memberId, uuid,
                Arrays.asList(externalUserId), "", sentTime, WeComMassTaskExternalUserStatusEnum.valueOf(status),
                Boolean.TRUE);
        List<String> keys = taskCacheManagerService.scanCustomerIdCache(corpId, memberId, taskUuid, uuid,
                externalUserId);
        if (CollectionUtils.isNotEmpty(keys)) {
            keys.forEach(item -> {
                taskCacheManagerService.setCustomerCache(item, status);
            });
        }

        return BaseResponse.success();
    }
}
