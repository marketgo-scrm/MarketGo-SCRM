package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.biz.service.CronExpressionResolver;
import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.enums.cron.PeriodEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.GenerateCronUtil;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberStatisticEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.model.bo.QueryTaskCenterMemberMetricsBuildSqlParam;
import com.easy.marketgo.core.model.bo.WeComMassTaskCreators;
import com.easy.marketgo.core.model.taskcenter.QueryTaskCenterBuildSqlParam;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import com.easy.marketgo.core.service.WeComAgentMessageService;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import com.easy.marketgo.web.model.request.WeComTaskCenterRequest;
import com.easy.marketgo.web.model.response.masstask.WeComMassTaskCreatorsResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComMembersStatisticResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterDetailResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterListResponse;
import com.easy.marketgo.web.model.response.taskcenter.WeComTaskCenterStatisticResponse;
import com.easy.marketgo.web.service.wecom.WeComTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.easy.marketgo.common.enums.ErrorCodeEnum.ERROR_WEB_MASS_TASK_IS_EMPTY;
import static com.easy.marketgo.common.enums.ErrorCodeEnum.ERROR_WEB_MASS_TASK_METRICS_TYPE_NOT_SUPPORT;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/12/22 5:38 PM
 * Describe:
 */

@Slf4j
@Service
public class WeComTaskCenterServiceImpl implements WeComTaskCenterService {

    @Resource
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Resource
    private WeComTaskCenterMemberStatisticRepository weComTaskCenterMemberStatisticRepository;

    @Resource
    private WeComTaskCenterExternalUserStatisticRepository weComTaskCenterExternalUserStatisticRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;

    @Autowired
    private WeComAgentMessageService weComAgentMessageService;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @Autowired
    private WeComTaskCenterMemberRepository weComTaskCenterMemberRepository;

    @Override
    public BaseResponse updateOrInsertTaskCenter(String projectId, String corpId, String taskType,
                                                 WeComTaskCenterRequest weComTaskCenterRequest) {
        log.info("update or save weCom task center. projectId={}, corpId={}, taskType={}, " +
                "weComTaskCenterRequest={}", projectId, corpId, taskType, weComTaskCenterRequest);
        WeComTaskCenterEntity entity = new WeComTaskCenterEntity();
        if (weComTaskCenterRequest.getId() != null) {
            entity = weComTaskCenterRepository.queryById(weComTaskCenterRequest.getId());
            String taskStatus = entity.getTaskStatus();
            if (!taskStatus.equalsIgnoreCase(WeComMassTaskStatus.UNSTART.getValue())) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_NOT_SUPPORT_CHANGE);
            }
        }

        BeanUtils.copyProperties(weComTaskCenterRequest, entity);
        entity.setUuid(IdUtil.simpleUUID());
        entity.setScheduleType(weComTaskCenterRequest.getScheduleType().getValue());
        entity.setTaskStatus(WeComMassTaskStatus.UNSTART.getValue());
        if (StringUtils.isNotEmpty(weComTaskCenterRequest.getScheduleTime())) {
            entity.setScheduleTime(DateUtil.parse(weComTaskCenterRequest.getScheduleTime()));
        } else {
            entity.setScheduleTime(DateUtil.date());
        }

        if (weComTaskCenterRequest.getScheduleType() == WeComMassTaskScheduleType.REPEAT_TIME) {
            if (StringUtils.isBlank(weComTaskCenterRequest.getRepeatEndTime()) ||
                    StringUtils.isBlank(weComTaskCenterRequest.getRepeatStartTime()) ||
                    weComTaskCenterRequest.getRepeatType() == null) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
            }

            entity.setRepeatType(weComTaskCenterRequest.getRepeatType().getValue());
            entity.setRepeatDay(weComTaskCenterRequest.getRepeatDay());
            entity.setRepeatStartTime(DateUtil.parse(weComTaskCenterRequest.getRepeatStartTime()));
            entity.setRepeatEndTime(DateUtil.parse(weComTaskCenterRequest.getRepeatEndTime()));
            Long finishTime = computeFinishTime(weComTaskCenterRequest.getRepeatStartTime(),
                    weComTaskCenterRequest.getRepeatEndTime(), weComTaskCenterRequest.getScheduleTime(),
                    weComTaskCenterRequest.getRepeatType().getValue(), weComTaskCenterRequest.getRepeatDay());
            if (finishTime != null) {
                entity.setFinishTime(DateUtil.date(finishTime));
            }
        }
        entity.setContent(JsonUtils.toJSONString(weComTaskCenterRequest.getContent()));

        entity.setCorpId(corpId);
        entity.setProjectUuid(projectId);
        entity.setTaskType(taskType);

        log.info("save weCom task center. entity={}", JsonUtils.toJSONString(entity));
        weComTaskCenterRepository.save(entity);

        if (weComTaskCenterRequest.getScheduleType() == WeComMassTaskScheduleType.IMMEDIATE) {
            log.info("notify compute user group for weCom task center. entity={}", entity);
            xxlJobManualTriggerService.manualTriggerHandler("computeUserGroupDetail");
        }
        return BaseResponse.success();
    }

    @Override
    public BaseResponse remindSendTask(String projectId, String corpId, String taskType, String taskUuid) {
        List<WeComTaskCenterMemberStatisticEntity> entities =
                weComTaskCenterMemberStatisticRepository.queryByTaskUuidAndStatus(taskUuid,
                        WeComMassTaskMemberStatusEnum.UNSENT.getValue());
        if (CollectionUtils.isEmpty(entities)) {
            return BaseResponse.success();
        }
        List<String> unsentMembers = new ArrayList<>();
        for (WeComTaskCenterMemberStatisticEntity entity : entities) {
            unsentMembers.add(entity.getMemberId());
        }
        unsentMembers = unsentMembers.stream().distinct().collect(Collectors.toList());
        WeComAgentMessageEntity agentMessageEntity = weComAgentMessageRepository.getWeComAgentByCorp(projectId, corpId);
        String agentId = (agentMessageEntity != null) ? agentMessageEntity.getAgentId() : "";

        weComAgentMessageService.sendRemindMessage(taskType, corpId, agentId, taskUuid,
                unsentMembers);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse listTaskCenter(String projectId, List<String> taskTypes, Integer pageNum, Integer pageSize,
                                       String corpId, List<String> statuses, String keyword,
                                       List<String> createUserIds, String sortKey, String sortOrder, String startTime
            , String endTime) {
        WeComTaskCenterListResponse response = new WeComTaskCenterListResponse();
        QueryTaskCenterBuildSqlParam param = QueryTaskCenterBuildSqlParam.builder()
                .projectUuid(projectId)
                .corpId(corpId)
                .taskTypes(taskTypes)
                .creatorIds(createUserIds)
                .endTime(DateUtil.parse(endTime))
                .startTime(DateUtil.parse(startTime))
                .sortKey(sortKey)
                .keyword(keyword)
                .statuses(statuses)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .sortOrderKey("DESC")
                .build();

        log.info("list weCom task center for param. param={}", param);
        Integer count = weComTaskCenterRepository.countByBuildSqlParam(param);
        log.info("count to list weCom  task center for param. count={}", count);
        List<WeComTaskCenterEntity> massTaskList = weComTaskCenterRepository.listByBuildSqlParam(param);
        List<WeComTaskCenterListResponse.MassTaskDetail> taskCenter = new ArrayList<>();


        massTaskList.forEach(entity -> {
            WeComTaskCenterListResponse.MassTaskDetail detail = new WeComTaskCenterListResponse.MassTaskDetail();
            detail.setName(entity.getName());
            detail.setCreatorName(entity.getCreatorName());
            detail.setCreatorId(entity.getCreatorId());
            detail.setId(entity.getId());
            detail.setUuid(entity.getUuid());
            detail.setScheduleTime(DateUtil.formatDateTime(entity.getScheduleTime()));
            if (entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME.getValue())) {
                detail.setScheduleTime(DateUtil.formatDateTime(entity.getRepeatStartTime()));
            }
            detail.setTaskStatus(WeComMassTaskStatus.fromValue(entity.getTaskStatus()));
            detail.setScheduleType(WeComMassTaskScheduleType.fromValue(entity.getScheduleType()));
            detail.setTaskType(WeComMassTaskTypeEnum.fromValue(entity.getTaskType()));
            detail.setCanRemind(canRemind(entity));
            String result = completeRateResult(entity);
            detail.setCompleteRate(result);
            if (result.equals("100%")) {
                detail.setCanRemind(Boolean.FALSE);
            }

            taskCenter.add(detail);
        });
        response.setTotalCount(count);
        response.setList(taskCenter);
        log.info("finish to query task center list response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse listMembers(String projectId, String corpId, String taskType, String metricsType,
                                    Integer pageNum, Integer pageSize, String taskUuid, String keyword, String status
            , String planTime) {
        log.info("start to query task center statistic for member. corpId={}, taskType={}, pageNum={}, pageSize={}, " +
                "taskUuid={}, status={}", corpId, taskType, pageNum, pageSize, taskUuid, status);
        if (metricsType.equals(WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue())) {
            return listMembersForExternalUser(projectId, corpId, metricsType, pageNum, pageSize, taskUuid,
                    keyword, status, planTime);
        } else if (metricsType.equals(WeComMassTaskMetricsType.MASS_TASK_RATE.getValue())) {
            return listMembersForRate(corpId, metricsType, pageNum, pageSize, taskUuid);
        }
        WeComMembersStatisticResponse response = new WeComMembersStatisticResponse();
        QueryTaskCenterMemberMetricsBuildSqlParam param =
                QueryTaskCenterMemberMetricsBuildSqlParam.builder().taskUuid(taskUuid).keyword(keyword).projectUuid(projectId).planDate(planTime).
                        pageNum(pageNum).pageSize(pageSize).status(status).build();
        Integer count = weComTaskCenterMemberStatisticRepository.countByBuildSqlParam(param);
        log.info("query task center member list count. count={}", count);
        List<WeComTaskCenterMemberStatisticEntity> entities =
                weComTaskCenterMemberStatisticRepository.listByBuildSqlParam(param);
        response.setCount(count);
        response.setStatus(status);
        List<WeComMembersStatisticResponse.MemberDetail> memberDetails = new ArrayList<>();
        entities.forEach(entity -> {
            WeComMembersStatisticResponse.MemberDetail memberDetail =
                    new WeComMembersStatisticResponse.MemberDetail();

            memberDetail.setMemberId(entity.getMemberId());
            memberDetail.setExternalUserCount(entity.getExternalUserCount());
            memberDetails.add(memberDetail);
        });
        memberDetails.forEach(member -> {
            WeComMemberMessageEntity weComMemberMessageEntity =
                    weComMemberMessageRepository.getMemberMessgeByMemberId(corpId, member.getMemberId());
            member.setMemberName(weComMemberMessageEntity.getMemberName());
            member.setAvatar(weComMemberMessageEntity.getAvatar());
        });
        WeComTaskCenterEntity entity = weComTaskCenterRepository.findByUuid(projectId, taskType, taskUuid);
        if (entity == null) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_IS_EMPTY);
        }
        response.setCanRemind(canRemind(entity));
        String result = completeRateResult(entity);
        if (result.equals("100%")) {
            response.setCanRemind(Boolean.FALSE);
        }
        response.setMembers(memberDetails);
        log.info("query mass task statistic for member response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getTaskCenterDetails(String projectUuid, Integer taskId) {
        WeComTaskCenterDetailResponse response = new WeComTaskCenterDetailResponse();

        WeComTaskCenterEntity entity = weComTaskCenterRepository.queryById(taskId);
        if (entity == null) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_IS_EMPTY);
        }
        BeanUtils.copyProperties(entity, response);
        if (entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME.getValue())) {
            response.setScheduleTime(DateUtil.formatDateTime(entity.getScheduleTime()).split(" ")[1]);
            response.setRepeatStartTime(DateUtil.formatDateTime(entity.getRepeatStartTime()).split(" ")[0]);
            response.setRepeatEndTime(DateUtil.formatDateTime(entity.getRepeatEndTime()).split(" ")[0]);
        } else {
            response.setScheduleTime(DateUtil.formatDateTime(entity.getScheduleTime()));
        }
        response.setCreateTime(DateUtil.formatDateTime(entity.getCreateTime()));
        List<WeComMassTaskSendMsg> list = JsonUtils.toArray(entity.getContent(), WeComMassTaskSendMsg.class);
        response.setMsgContent(list);
        log.info("finish to query task center detail response. projectUuid = {}, taskId={}, response={}", projectUuid,
                taskId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getTaskCenterStatistic(String taskUuid, String metricsType, String planTime) {
        WeComTaskCenterStatisticResponse response = new WeComTaskCenterStatisticResponse();

        if (WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue().equals(metricsType)) {
            WeComTaskCenterStatisticResponse.ExternalUserStatisticDetail externalUserDetail =
                    new WeComTaskCenterStatisticResponse.ExternalUserStatisticDetail();

            for (WeComMassTaskExternalUserStatusEnum value : WeComMassTaskExternalUserStatusEnum.values()) {
                Integer count =
                        weComTaskCenterExternalUserStatisticRepository.countByTaskUuidAndStatusAndPlanTime(taskUuid,
                                value.getValue(), planTime);
                if (value == WeComMassTaskExternalUserStatusEnum.UNDELIVERED) {
                    externalUserDetail.setNonDeliveredCount(count);
                } else if (value == WeComMassTaskExternalUserStatusEnum.DELIVERED) {
                    externalUserDetail.setDeliveredCount(count);
                } else if (value == WeComMassTaskExternalUserStatusEnum.UNFRIEND) {
                    externalUserDetail.setUnfriendCount(count);
                }
            }
            externalUserDetail.setExternalUserTotalCount(externalUserDetail.getNonDeliveredCount() +
                    externalUserDetail.getDeliveredCount() + externalUserDetail.getUnfriendCount());
            response.setExternalUserDetail(externalUserDetail);
        } else if (WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue().equals(metricsType)) {
            WeComTaskCenterStatisticResponse.MemberStatisticDetail memberDetail =
                    new WeComTaskCenterStatisticResponse.MemberStatisticDetail();

            for (WeComMassTaskMemberStatusEnum value : WeComMassTaskMemberStatusEnum.values()) {
                int count = weComTaskCenterMemberStatisticRepository.countByTaskUuidAndStatus(taskUuid,
                        value.getValue(), planTime);
                if (value == WeComMassTaskMemberStatusEnum.UNSENT) {
                    memberDetail.setNonSendCount(count);
                } else if (value == WeComMassTaskMemberStatusEnum.SENT) {
                    memberDetail.setSendCount(count);
                } else if (value == WeComMassTaskMemberStatusEnum.SENT_FAIL) {
                    memberDetail.setSendFailedCount(count);
                }
            }
            memberDetail.setMemberTotalCount(memberDetail.getSendCount() + memberDetail.getNonSendCount());
            response.setMemberDetail(memberDetail);
        } else if (WeComMassTaskMetricsType.MASS_TASK_RATE.getValue().equals(metricsType)) {
            WeComTaskCenterStatisticResponse.StatisticDetail detail =
                    new WeComTaskCenterStatisticResponse.StatisticDetail();
            List<Long> totalCount = computeTotalCount(taskUuid);
            long currentTime = System.currentTimeMillis();
            Integer sentCount = 0;
            Integer unSentCount = 0;
            for (Long item : totalCount) {
                if (item < currentTime) {
                    sentCount += 1;
                } else {
                    unSentCount += 1;
                }
            }
            Integer totalSendCount = totalCount.size();

            Integer sentCountForDb = weComTaskCenterMemberStatisticRepository.countByTaskUuidAndPlanTime(taskUuid);

            sentCountForDb = sentCountForDb == null ? 0 : sentCountForDb;
            Integer failedCount = sentCount - sentCountForDb;

            detail.setTotalSendCount(totalSendCount);
            detail.setSentCount(sentCountForDb);
            detail.setUnsentCount(unSentCount);
            detail.setFailedCount(failedCount);
            response.setStatisticDetail(detail);
        } else {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_METRICS_TYPE_NOT_SUPPORT);
        }
        log.info("query task center statistic response. response={}", JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getTaskCenterCreators(String projectUuid, String corpId, String taskType) {
        log.info("start to query weCom task center creator list, projectUuid={}, taskType={}, corpId={}",
                projectUuid, taskType, corpId);
        WeComMassTaskCreatorsResponse response = new WeComMassTaskCreatorsResponse();
        List<WeComMassTaskCreatorsResponse.CreatorMessage> creatorList = new ArrayList<>();
        List<WeComMassTaskCreators> creators = weComTaskCenterRepository.listCreatorsByTaskType(projectUuid, corpId,
                taskType);
        log.info("query weCom task center creator list. creators={}", creators);
        if (CollectionUtils.isNotEmpty(creators)) {
            creators.forEach(creator -> {
                WeComMassTaskCreatorsResponse.CreatorMessage message =
                        new WeComMassTaskCreatorsResponse.CreatorMessage();
                message.setCreatorId(creator.getCreatorId());
                message.setCreatorName(creator.getCreatorName());
                creatorList.add(message);
            });
        }
        response.setCreators(creatorList);
        log.info("query task center creator list response. response={}", JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse deleteTaskCenter(String taskType, String taskUuid) {
        log.info("start to delete weCom task center. taskType={}, taskUuid={}",
                taskType, taskUuid);
        WeComTaskCenterEntity entity = weComTaskCenterRepository.getByTaskUUID(taskUuid);
        if (entity == null) {
            return BaseResponse.success();
        }
        String content = entity.getContent();
        if (StringUtils.isNotEmpty(content)) {
            List<WeComMassTaskSendMsg> contentList = JsonUtils.toArray(content, WeComMassTaskSendMsg.class);
            if (CollectionUtils.isNotEmpty(contentList)) {
                List<String> mediaUuidList = new ArrayList<>();
                contentList.forEach(item -> {
                    if (item.getType() == WeComMassTaskSendMsg.TypeEnum.VIDEO) {
                        mediaUuidList.add(item.getVideo().getMediaUuid());
                    } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.IMAGE) {
                        mediaUuidList.add(item.getImage().getMediaUuid());
                    } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.FILE) {
                        mediaUuidList.add(item.getFile().getMediaUuid());
                    } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.MINIPROGRAM) {
                        mediaUuidList.add(item.getMiniProgram().getMediaUuid());
                    } else if (item.getType() == WeComMassTaskSendMsg.TypeEnum.LINK) {
                        mediaUuidList.add(item.getLink().getMediaUuid());
                    }
                });
                if (CollectionUtils.isNotEmpty(mediaUuidList)) {
                    weComMediaResourceRepository.deleteByUuids(mediaUuidList);
                }
            }
        }

        List<WeComTaskCenterMemberStatisticEntity> entities =
                weComTaskCenterMemberStatisticRepository.queryByTaskUuid(taskUuid);
        if (CollectionUtils.isNotEmpty(entities)) {
            for (WeComTaskCenterMemberStatisticEntity item : entities) {
                try {
                    taskCacheManagerService.delMemberCache(entity.getCorpId(), item.getMemberId(), item.getTaskUuid(),
                            item.getUuid());
                    taskCacheManagerService.delCustomerCache(entity.getCorpId(), item.getMemberId(), item.getTaskUuid(),
                            item.getUuid());
                } catch (Exception e) {
                    log.error("failed to delete weCom task center, memberId={}, taskUuid={}, uuid={}",
                            item.getMemberId(), item.getTaskUuid(), item.getUuid(), e);
                }
            }
        }
        try {
            taskCacheManagerService.delCacheContent(taskUuid);
        } catch (Exception e) {
            log.error("failed to delete content weCom task center, taskUuid={}", taskUuid, e);
        }
        weComTaskCenterMemberStatisticRepository.deleteByTaskUuid(taskUuid);
        weComTaskCenterExternalUserStatisticRepository.deleteByTaskUuid(taskUuid);
        weComTaskCenterMemberRepository.deleteByUuid(taskUuid);
        weComTaskCenterRepository.deleteById(Long.valueOf(entity.getId()));
        log.info("finish to delete weCom task center. taskType={}, taskUuid={}",
                taskType, taskUuid);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse checkTaskCenterName(String projectId, String taskType, Integer taskId, String name) {
        try {
            log.info("begin to check weCom task center cname, projectUuid={}, taskType={}, taskId={}, taskCname={}.",
                    projectId, taskType, taskId, name);
            if (!WeComMassTaskTypeEnum.isSupported(taskType)) {
                log.error("Unsupported wecom task center type, taskType={}.", taskType);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_NOT_SUPPORT_MASS_TASK.getCode())
                        .message(ErrorCodeEnum.ERROR_NOT_SUPPORT_MASS_TASK.getMessage()).build();
            }
            WeComTaskCenterEntity weComTaskCenterEntity = weComTaskCenterRepository.getTaskCenterByName(projectId,
                    name);
            // 仅当「非同一个计划且同名」的情况，认为重名
            if (weComTaskCenterEntity != null && !weComTaskCenterEntity.getId().equals(taskId)) {
                log.info("failed to check weCom task center name, projectUuid={}, taskType={}, taskId={}, name={}, " +
                        "weComTaskCenterEntity={}.", projectId, taskType, taskId, name, weComTaskCenterEntity);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getCode())
                        .message(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getMessage()).build();
            }
            log.info("succeed to check weCom task center name, projectUuid={}, taskType={}, taskId={}, taskCname={}, " +
                    "weComTaskCenterEntity={}.", projectId, taskType, taskId, name, weComTaskCenterEntity);
            return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
        } catch (Exception e) {
            log.error("failed to check weCom task center name, projectUuid={}, taskType={}, taskId={}, name={}.",
                    projectId, taskType, taskId, name, e);
        }
        return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getCode())
                .message(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getMessage()).build();
    }

    private List<Long> computeTotalCount(String taskUuid) {
        List<Long> executeTimes = new ArrayList<>();
        WeComTaskCenterEntity entity = weComTaskCenterRepository.getByTaskUUID(taskUuid);

        long startOfDay = DateUtil.beginOfDay(entity.getRepeatStartTime()).getTime();
        long endOfDay = DateUtil.endOfDay(entity.getRepeatEndTime()).getTime();

        String[] startDate = DateUtil.formatDateTime(entity.getRepeatStartTime()).split(" ");
        String[] startTime = DateUtil.formatDateTime(entity.getScheduleTime()).split(" ");
        String cron = "";
        if (entity.getRepeatType().equals(PeriodEnum.DAILY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateDailyCronByPeriodAndTime(startDate[0], startTime[1]);
        } else if (entity.getRepeatType().equals(PeriodEnum.WEEKLY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateWeeklyCronByPeriodAndTime(startDate[0], startTime[1],
                    entity.getRepeatDay());
        } else if (entity.getRepeatType().equals(PeriodEnum.MONTHLY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateMonthlyCronByPeriodAndTime(startDate[0], startTime[1],
                    entity.getRepeatDay());
        }

        log.info("compute cron string. cron={}", cron);
        CronExpressionResolver cronExpressionResolver = CronExpressionResolver.getInstance(cron);
        long nextTime = cronExpressionResolver.nextLongTime(startOfDay);
        while (nextTime > 0 && nextTime < endOfDay) {
            executeTimes.add(nextTime);
            nextTime = cronExpressionResolver.nextLongTime(nextTime);
        }
        log.info("compute next time for cron string. cron={}, execute size={}", cron, executeTimes.size());
        for (Long item : executeTimes) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeNow = sdf.format(item);
            log.info("compute execute time for cron string. cron={}, execute time={}", cron, timeNow);
        }
        return executeTimes;
    }

    private Long computeFinishTime(String startTime, String endTime, String scheduleTime, String repeatType,
                                   String repeatDay) {
        Long executeTimes = null;

        long startOfDay = DateUtil.beginOfDay(DateUtil.parse(startTime)).getTime();
        long endOfDay = DateUtil.endOfDay(DateUtil.parse(endTime)).getTime();

        String cron = "";
        if (repeatType.equals(PeriodEnum.DAILY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateDailyCronByPeriodAndTime(startTime, scheduleTime);
        } else if (repeatType.equals(PeriodEnum.WEEKLY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateWeeklyCronByPeriodAndTime(startTime, scheduleTime,
                    repeatDay);
        } else if (repeatType.equals(PeriodEnum.MONTHLY.name())) {
            cron = GenerateCronUtil.INSTANCE.generateMonthlyCronByPeriodAndTime(startTime, scheduleTime,
                    repeatDay);
        }

        log.info("compute cron string. cron={}", cron);
        CronExpressionResolver cronExpressionResolver = CronExpressionResolver.getInstance(cron);
        long nextTime = cronExpressionResolver.nextLongTime(startOfDay);
        while (nextTime > 0 && nextTime < endOfDay) {
            executeTimes = nextTime;
            nextTime = cronExpressionResolver.nextLongTime(nextTime);
        }
        return executeTimes;
    }

    private BaseResponse listMembersForExternalUser(String projectId, String corpId, String taskType, Integer pageNum
            , Integer pageSize, String taskUuid, String keyword, String status, String planTime) {
        log.info("start to query task center statistic for external user. corpId={}, taskType={}, pageNum={}, " +
                "pageSize={},taskUuid={}, status={}", corpId, taskType, pageNum, pageSize, taskUuid, status);

        WeComMembersStatisticResponse response = new WeComMembersStatisticResponse();

        QueryTaskCenterMemberMetricsBuildSqlParam param =
                QueryTaskCenterMemberMetricsBuildSqlParam.builder().taskUuid(taskUuid).keyword(keyword).projectUuid(projectId).planDate(planTime).
                        pageNum(pageNum).pageSize(pageSize).build();

        Integer count = weComTaskCenterMemberStatisticRepository.countByBuildSqlParam(param);

        log.info("query task center member list count. count={}", count);

        List<WeComTaskCenterMemberStatisticEntity> weComTaskCenterMemberStatisticEntities =
                weComTaskCenterMemberStatisticRepository.listByBuildSqlParam(param);
        response.setCount(count);

        List<WeComMembersStatisticResponse.MemberDetail> members = new ArrayList<>();
        weComTaskCenterMemberStatisticEntities.forEach(entity -> {
            WeComMembersStatisticResponse.MemberDetail detail =
                    new WeComMembersStatisticResponse.MemberDetail();
            detail.setMemberName(entity.getMemberName());
            detail.setMemberId(entity.getMemberId());
            if (StringUtils.isBlank(status) || status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue())) {
                detail.setExternalUserCount(entity.getExternalUserCount() == null ? 0 : entity.getExternalUserCount());
            } else if (StringUtils.isBlank(status) || status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.DELIVERED.getValue())) {
                detail.setExternalUserCount(entity.getDeliveredCount() == null ? 0 : entity.getDeliveredCount());
            } else if (StringUtils.isBlank(status) || status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.UNFRIEND.getValue())) {
                detail.setExternalUserCount(entity.getNonFriendCount() == null ? 0 : entity.getNonFriendCount());
            }
            members.add(detail);
        });

        response.setMembers(members);
        log.info("finish to task center statistic for external user response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    private BaseResponse listMembersForRate(String corpId, String taskType, Integer pageNum, Integer pageSize,
                                            String taskUuid) {
        log.info("start to query task center statistic for rate. corpId={}, taskType={}, pageNum={}, " +
                "pageSize={},taskUuid={}", corpId, taskType, pageNum, pageSize, taskUuid);

        WeComMembersStatisticResponse response = new WeComMembersStatisticResponse();
        List<WeComMembersStatisticResponse.DayDetail> dayDetails = new ArrayList<>();
        List<Long> planTimes = computeTotalCount(taskUuid);
        long currentTime = System.currentTimeMillis();
        planTimes = planTimes.stream().filter(e -> e < currentTime).sorted().collect(Collectors.toList());
        if ((pageNum - 1) * pageSize > planTimes.size()) {
            response.setDayDetails(dayDetails);
            log.info("finish to query empty task center statistic for rate response. corpId={}, response={}", corpId,
                    JsonUtils.toJSONString(response));
            return BaseResponse.success(response);
        }
        for (int i = (pageNum - 1) * pageSize; i < (planTimes.size() < (pageNum * pageSize) ? planTimes.size() :
                (pageNum * pageSize)); i++) {
            Long time = planTimes.get(i);
            DateTime dateTime = DateUtil.date(time);
            String dateString = dateTime.toDateStr();
            log.info("check date string. dateString={}", dateString);
            WeComMembersStatisticResponse.DayDetail dayDetail = new WeComMembersStatisticResponse.DayDetail();
            List<WeComTaskCenterMemberStatisticEntity> entities =
                    weComTaskCenterMemberStatisticRepository.queryByTaskUuidAndplanTime(taskUuid, dateString);
            dayDetail.setPlanTime(dateString);
            Integer externalUserCount = 0;

            Integer unSentCount = 0;
            Integer sentCount = 0;
            String result = "0%";
            if (CollectionUtils.isNotEmpty(entities)) {
                for (WeComTaskCenterMemberStatisticEntity entity : entities) {
                    externalUserCount += entity.getExternalUserCount() == null ? 0 : entity.getExternalUserCount();
                    if (entity.getStatus().equals(WeComMassTaskMemberStatusEnum.SENT)) {
                        sentCount += 1;
                    } else {
                        unSentCount += 1;
                    }
                }

                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                result =
                        numberFormat.format((float) sentCount / (float) entities.size() * 100);
                result += "%";
            }

            dayDetail.setMemberCount(entities.size());
            dayDetail.setCompleteRate(result);
            dayDetail.setExternalUserCount(externalUserCount);
            dayDetails.add(dayDetail);
        }

        response.setDayDetails(dayDetails);
        log.info("finish to task center statistic for rate response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    private Boolean canRemind(WeComTaskCenterEntity entity) {
        if (entity.getTaskStatus().equals(WeComMassTaskStatus.FINISHED.getValue()) ||
                entity.getTaskStatus().equals(WeComMassTaskStatus.UNSTART.getValue())) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private String completeRateResult(WeComTaskCenterEntity entity) {
        String dateString = DateUtil.date(entity.getScheduleTime().getTime()).toDateStr();
        if (entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME.getValue())) {
            List<Long> planTimes = computeTotalCount(entity.getUuid());
            long currentTime = System.currentTimeMillis();
            long planTime = currentTime;
            if (CollectionUtils.isNotEmpty(planTimes)) {
                for (Long item : planTimes) {
                    if (item < currentTime) {
                        planTime = item;
                    }
                }
            }
            DateTime dateTime = DateUtil.date(planTime);
            dateString = dateTime.toDateStr();
        }
        log.info("check date string. dateString={}", dateString);
        String result = "0%";
        Integer nonSendCount = 0;
        Integer sendCount = 0;
        Integer sendFailedCount = 0;
        for (WeComMassTaskMemberStatusEnum value : WeComMassTaskMemberStatusEnum.values()) {
            int countValue = weComTaskCenterMemberStatisticRepository.countByTaskUuidAndStatus(entity.getUuid(),
                    value.getValue(), dateString);
            if (value == WeComMassTaskMemberStatusEnum.UNSENT) {
                nonSendCount = countValue;
            } else if (value == WeComMassTaskMemberStatusEnum.SENT) {
                sendCount = countValue;
            } else if (value == WeComMassTaskMemberStatusEnum.SENT_FAIL) {
                sendFailedCount = countValue;
            }
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        log.info("complete task center rate. sendCount={}, nonSendCount={}, sendFailedCount={}", sendCount,
                nonSendCount, sendFailedCount);
        if (nonSendCount != 0 || sendCount != 0 || sendFailedCount != 0) {
            result =
                    numberFormat.format((float) sendCount / (float) (nonSendCount + sendCount + sendFailedCount) * 100);
            result += "%";

        }
        return result;
    }
}
