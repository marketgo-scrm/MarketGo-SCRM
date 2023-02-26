package com.easy.marketgo.react.service.client;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.model.taskcenter.QuerySubTaskCenterMemberBuildSqlParam;
import com.easy.marketgo.core.model.taskcenter.QueryTaskCenterMemberBuildSqlParam;

import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberRepository;
import com.easy.marketgo.core.service.taskcenter.SendTaskCenterBaseConsumer;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import com.easy.marketgo.react.model.response.WeComMemberTaskCenterListResponse;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private WeComTaskCenterMemberRepository weComTaskCenterMemberRepository;

    @Override
    public WeComMemberTaskCenterListResponse listTaskCenter(String corpId, String memberId, List<String> taskTypes,
                                                            List<String> statuses,
                                                            String startTime, String endTime, Integer pageNum,
                                                            Integer pageSize) {

        QueryTaskCenterMemberBuildSqlParam param =
                QueryTaskCenterMemberBuildSqlParam.builder().corpId(corpId)
                        .memberId(memberId).endTime(DateUtil.parse(endTime)).startTime(DateUtil.parse(startTime))
                        .pageNum(pageNum).pageSize(pageSize).sortOrderKey("DESC").build();
        Integer count = weComTaskCenterMemberRepository.countByBuildSqlParam(param);

        WeComMemberTaskCenterListResponse response = new WeComMemberTaskCenterListResponse();
        if (count == null) {
            response.setTotalCount(0);
            return response;
        }
        response.setTotalCount(count);
        List<WeComTaskCenterMemberEntity> entities = weComTaskCenterMemberRepository.listByBuildSqlParam(param);
        if (CollectionUtils.isEmpty(entities)) {
            return response;
        }
        List<WeComMemberTaskCenterListResponse.MemberTaskCenterDetail> list = new ArrayList<>();
        for (WeComTaskCenterMemberEntity entity : entities) {
            WeComMemberTaskCenterListResponse.MemberTaskCenterDetail detail =
                    new WeComMemberTaskCenterListResponse.MemberTaskCenterDetail();
            BeanUtils.copyProperties(entity, detail);
            detail.setPlanTime(DateUtil.formatDateTime(entity.getPlanTime()));
            list.add(detail);
        }
        response.setList(list);
        return response;
    }

    @Override
    public WeComMemberTaskCenterListResponse listSubTaskCenter(String corpId, String memberId, String taskUuid,
                                                               Integer pageNum,
                                                               Integer pageSize) {

        QuerySubTaskCenterMemberBuildSqlParam param =
                QuerySubTaskCenterMemberBuildSqlParam.builder().corpId(corpId)
                        .memberId(memberId).taskUuid(taskUuid)
                        .pageNum(pageNum).pageSize(pageSize).sortOrderKey("DESC").build();
        Integer count = weComTaskCenterMemberRepository.countSubTaskByParam(param);

        WeComMemberTaskCenterListResponse response = new WeComMemberTaskCenterListResponse();
        if (count == null) {
            response.setTotalCount(0);
            return response;
        }
        response.setTotalCount(count);
        List<WeComTaskCenterMemberEntity> entities = weComTaskCenterMemberRepository.listSubTaskByParam(param);
        if (CollectionUtils.isEmpty(entities)) {
            return response;
        }
        List<WeComMemberTaskCenterListResponse.MemberTaskCenterDetail> list = new ArrayList<>();
        for (WeComTaskCenterMemberEntity entity : entities) {
            WeComMemberTaskCenterListResponse.MemberTaskCenterDetail detail =
                    new WeComMemberTaskCenterListResponse.MemberTaskCenterDetail();
            BeanUtils.copyProperties(entity, detail);
            detail.setPlanTime(DateUtil.formatDateTime(entity.getPlanTime()));
            list.add(detail);
        }
        response.setList(list);
        return response;
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
                message.setAvatar("");
                if (values.length == 7) {
                    message.setAvatar(Base64.decodeStr(values[6]));
                }
            }
            String status = taskCacheManagerService.getCustomerCache(item);
            message.setStatus(status);
            users.add(message);
        });
        taskCacheManagerService.setMemberReadDetailCache(corpId, taskUuid, memberId);
        detailClientResponse.setUuid(uuid);
        detailClientResponse.setExternalUserId(users);
        log.info("finish to query task center detail. response={}", detailClientResponse);
        return detailClientResponse;
    }

    @Override
    public List<WeComTaskCenterDetailResponse> getTaskCenterContent(String corpId, String memberId, String taskUuid) {
        log.info("query task center cache content. corpId={}, taskUuid={}, memberId={}", corpId, taskUuid, memberId);
        String uuid = taskCacheManagerService.getMemberReadDetailCache(corpId, memberId);
        List<WeComTaskCenterDetailResponse> list = new ArrayList<>();
        if (StringUtils.isNotBlank(uuid)) {
            String content = taskCacheManagerService.getCacheContent(taskUuid);
            if (StringUtils.isBlank(content)) {
                throw new CommonException(ErrorCodeEnum.ERROR_REACT_TASK_CONTENT_IS_NOT_EXIST);
            }
            log.info("query content cache message. content={}", content);
            WeComTaskCenterDetailResponse detailClientResponse = JsonUtils.toObject(content,
                    WeComTaskCenterDetailResponse.class);
            list.add(detailClientResponse);
        } else {
            List<WeComTaskCenterMemberEntity> entities = weComTaskCenterMemberRepository.getMemberTaskByStatus(corpId,
                    memberId, WeComMassTaskStatus.FINISHED.getValue());
            if (CollectionUtils.isEmpty(entities)) {
                return list;
            }
            for (WeComTaskCenterMemberEntity entity : entities) {
                WeComTaskCenterDetailResponse detailClientResponse =
                        new WeComTaskCenterDetailResponse();
                List<WeComTaskCenterDetailResponse.AttachmentsMessage> messages = JsonUtils.toArray(entity.getContent(),
                        WeComTaskCenterDetailResponse.AttachmentsMessage.class);
                BeanUtils.copyProperties(entity, detailClientResponse);
                detailClientResponse.setTaskName(entity.getName());
                detailClientResponse.setPlanTime(DateUtil.formatTime(entity.getPlanTime()));
                detailClientResponse.setAttachments(messages);
                list.add(detailClientResponse);
            }
        }

        return list;
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
