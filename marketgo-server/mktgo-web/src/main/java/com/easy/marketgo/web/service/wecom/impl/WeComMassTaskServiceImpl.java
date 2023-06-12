package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.api.model.request.WeComSendAgentMessageClientRequest;
import com.easy.marketgo.api.model.request.masstask.*;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComMassTaskForMomentCreateResponse;
import com.easy.marketgo.api.model.response.masstask.WeComMomentMassTaskPublishResultClientResponse;
import com.easy.marketgo.api.model.response.masstask.WeComQueryMemberResultClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.api.service.WeComSendAgentMessageRpcService;
import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskExternalUserStatisticEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskMemberStatisticEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSyncStatisticEntity;
import com.easy.marketgo.core.model.bo.*;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSyncStatisticRepository;
import com.easy.marketgo.core.service.WeComMassTaskCacheService;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import com.easy.marketgo.web.model.request.WeComMassTaskRequest;
import com.easy.marketgo.web.model.response.masstask.*;
import com.easy.marketgo.web.service.wecom.WeComMassTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.easy.marketgo.biz.service.wecom.masstask.QueryMassTaskMetricsService.CREATE_MOMENT_STATUE_COMPLETE;
import static com.easy.marketgo.common.enums.ErrorCodeEnum.*;
import static java.util.Collections.emptyList;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 6:01 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComMassTaskServiceImpl implements WeComMassTaskService {

    @Resource
    private WeComMassTaskRepository weComMassTaskRepository;

    @Resource
    private WeComMassTaskMemberStatisticRepository weComMassTaskMemberStatisticRepository;

    @Resource
    private WeComMassTaskExternalUserStatisticRepository weComMassTaskExternalUserStatisticRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Resource
    private WeComMassTaskSyncStatisticRepository weComMassTaskSyncStatisticRepository;

    @Resource
    private WeComMassTaskRpcService weComMassTaskRpcService;

    @Resource
    private WeComSendAgentMessageRpcService weComSendAgentMessageRpcService;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;

    @Autowired
    private WeComMassTaskCacheService weComMassTaskCacheService;

    @Override
    public BaseResponse updateOrInsertMassTask(String projectId, String corpId, String taskType,
                                               WeComMassTaskRequest weComMassTaskRequest) {
        log.info("update or save weCom mass task. projectId={}, corpId={}, taskType={}, " +
                "weComMassTaskRequest={}", projectId, corpId, taskType, weComMassTaskRequest);
        WeComMassTaskEntity entity = new WeComMassTaskEntity();
        if (weComMassTaskRequest.getId() != null) {
            WeComMassTaskEntity weComMassTaskEntity =
                    weComMassTaskRepository.queryById(weComMassTaskRequest.getId());
            String taskStatus = weComMassTaskEntity.getTaskStatus();
            if (!taskStatus.equalsIgnoreCase(WeComMassTaskStatus.UNSTART.getValue())) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_NOT_SUPPORT_CHANGE);
            }
            BeanUtils.copyProperties(weComMassTaskRequest, entity);
        } else {
            BeanUtils.copyProperties(weComMassTaskRequest, entity);
            entity.setUuid(IdUtil.simpleUUID());
            entity.setScheduleType(weComMassTaskRequest.getScheduleType().getValue());
            entity.setTaskStatus(WeComMassTaskStatus.UNSTART.getValue());
            if (StringUtils.isNotEmpty(weComMassTaskRequest.getScheduleTime())) {
                entity.setScheduleTime(DateUtil.parse(weComMassTaskRequest.getScheduleTime()));
            } else {
                entity.setScheduleTime(DateUtil.date());
            }
            entity.setContent(JsonUtils.toJSONString(weComMassTaskRequest.getContent()));
        }

        entity.setCorpId(corpId);
        entity.setProjectUuid(projectId);
        entity.setTaskType(taskType);

        log.info("save weCom mass task. entity={}", JsonUtils.toJSONString(entity));
        weComMassTaskRepository.save(entity);

        if (weComMassTaskRequest.getScheduleType() == WeComMassTaskScheduleType.IMMEDIATE) {
            log.info("notify compute user group for weCom mass task. entity={}", entity);
            xxlJobManualTriggerService.manualTriggerHandler("computeUserGroupDetail");
        }
        return BaseResponse.success();
    }

    @Override
    public BaseResponse remindSendTask(String projectId, String corpId, String taskType, String taskUuid) {
        WeComMassTaskEntity entity = weComMassTaskRepository.findByUuid(projectId, taskType, taskUuid);
        if (entity == null) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_IS_EMPTY);
        }
        String value = weComMassTaskCacheService.getCacheRemindCount(taskUuid);
        if (StringUtils.isNotEmpty(value) && Integer.valueOf(value) > 2) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_REMIND_COUNT_IS_MAX);
        }

        WeComAgentMessageEntity agentMessageEntity = weComAgentMessageRepository.getWeComAgentByCorp(projectId,
                corpId);
        log.info("mass task to remind send. weComMassTask={}", entity);
        String agentId = (agentMessageEntity != null) ? agentMessageEntity.getAgentId() : "";

        int offset = 0;
        int limitSize = 1000;
        do {
            log.info("start query mass task list.taskUuid={}, offset={}, limitSize={}", entity.getUuid(), offset,
                    limitSize);
            List<WeComMassTaskSyncStatisticEntity> messages =
                    weComMassTaskSyncStatisticRepository.getWeComMassTaskResponseByTaskUuid(taskUuid, offset,
                            limitSize);
            if (CollectionUtils.isNotEmpty(messages)) {
                List<String> unsentMembers = new ArrayList<>();
                for (WeComMassTaskSyncStatisticEntity item : messages) {
                    List<String> memberList = new ArrayList<>();
                    if (entity.getTaskType().equals(WeComMassTaskTypeEnum.MOMENT.name())) {
                        memberList = queryMemberUnsentListForMoment(projectId,
                                entity.getCorpId(), agentId, "", item.getSendId());
                    } else {
                        WeComRemindMemberMessageClientRequest request = new WeComRemindMemberMessageClientRequest();
                        request.setAgentId(agentId);
                        request.setCorpId(corpId);
                        request.setMsgId(item.getSendId());
                        weComMassTaskRpcService.remindMemberMessage(request);
                    }
                    unsentMembers.addAll(memberList);
                }
                log.info("remind mass task to member.unsentMembers={}", unsentMembers);
                if (CollectionUtils.isNotEmpty(unsentMembers)) {
                    sendRemindMessage(projectId, taskType, entity.getCorpId(), agentId, taskUuid,
                            unsentMembers);
                }
                offset = (messages.size() < limitSize) ? 0 : (offset + limitSize);
                log.info("query mass task is empty.taskUuid={}", taskUuid);
            }
        } while (offset > 0);

        DateTime dateTime = new DateTime();
        LocalDateTime midnight = LocalDateTime.ofInstant(dateTime.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);

        LocalDateTime currentDateTime = LocalDateTime.ofInstant(dateTime.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);

        weComMassTaskCacheService.setCacheContent(taskUuid,
                String.valueOf(Integer.valueOf((StringUtils.isNotEmpty(value) ? value : "0")) + 1), seconds);
        weComMassTaskRepository.updateTaskRemindTime(DateUtil.date(), taskUuid);
        return BaseResponse.success();
    }

    private List<String> queryMemberUnsentListForMoment(String projectId, String corpId, String agentId, String cursor,
                                                        String momentId) {
        WeComMomentMassTaskPublishResultClientRequest
                weComMassTaskForMomentPublishResultRequest = new WeComMomentMassTaskPublishResultClientRequest();
        weComMassTaskForMomentPublishResultRequest.setAgentId(agentId);
        weComMassTaskForMomentPublishResultRequest.setCorpId(corpId);
        weComMassTaskForMomentPublishResultRequest.setMomentId(momentId);
        weComMassTaskForMomentPublishResultRequest.setCursor(cursor);
        RpcResponse<WeComMomentMassTaskPublishResultClientResponse> response =
                weComMassTaskRpcService.queryMomentMassTaskForPublishResult(weComMassTaskForMomentPublishResultRequest);
        log.info("query mass task from moment publish result. momentId={}, response={}", momentId, response);
        if (!response.getSuccess() || response.getData() == null) {
            log.info(
                    "failed to query mass task from single or group result. projectId={}, corpId={}, agentId={}, " +
                            "momentId={},response={}", projectId, corpId, agentId, momentId, response);
            return emptyList();
        }
        List<String> list = new ArrayList<>();
        WeComMomentMassTaskPublishResultClientResponse weComMassTaskForMomentPublishResultResponse = response.getData();
        for (WeComMomentMassTaskPublishResultClientResponse.TaskListMessage item :
                weComMassTaskForMomentPublishResultResponse.getTaskList()) {
            if (item.getPublishStatus() == WeComMassTaskMemberStatusEnum.getMemberStatus(WeComMassTaskMemberStatusEnum.UNSENT)) {
                list.add(item.getUserId());
            }
        }
        log.info("return query mass task from moment result. msgId={}, list={}", momentId, list);
        return list;
    }

    private List<String> queryMemberUnsentListForSingleOrGroup(String projectId, String corpId, String agentId,
                                                               String cursor, String msgId) {
        WeComQueryMemberResultClientRequest
                weComMemberResultClientRequest = new WeComQueryMemberResultClientRequest();
        weComMemberResultClientRequest.setAgentId(agentId);
        weComMemberResultClientRequest.setCorpId(corpId);
        weComMemberResultClientRequest.setMsgId(msgId);
        weComMemberResultClientRequest.setCursor(cursor);
        RpcResponse<WeComQueryMemberResultClientResponse> response =
                weComMassTaskRpcService.queryMassTaskForMemberResult(weComMemberResultClientRequest);
        log.info("query mass task from single or group result. msgId={}, response={}", msgId, response);
        if (!response.getSuccess() || response.getData() == null) {
            log.info(
                    "failed to query mass task from single or group result. projectId={}, corpId={}, agentId={}, " +
                            "msgId={},response={}", projectId, corpId, agentId, msgId, response);
        }
        List<String> list = new ArrayList<>();
        WeComQueryMemberResultClientResponse weComGetSingleOrGroupTaskResultResponse = response.getData();
        for (WeComQueryMemberResultClientResponse.TaskListMessage item :
                weComGetSingleOrGroupTaskResultResponse.getTaskList()) {
            if (item.getStatus() == WeComMassTaskMemberStatusEnum.getMemberStatus(WeComMassTaskMemberStatusEnum.UNSENT)) {
                list.add(item.getUserId());
            }
        }
        log.info("return query mass task from single or group result. msgId={}, list={}", msgId, list);
        return list;
    }

    private void sendRemindMessage(String projectId, String taskType, String corpId, String agentId, String taskUuid,
                                   List<String> members) {
        WeComSendAgentMessageClientRequest appMsgRequest = new WeComSendAgentMessageClientRequest();

        appMsgRequest.setAgentId(agentId);
        appMsgRequest.setCorpId(corpId);
        appMsgRequest.setMsgType(WeComSendAgentMessageClientRequest.MsgTypeEnum.TEXT);
        appMsgRequest.setMsgId(taskUuid);
        Map<String, String> textMessage = new HashMap<>();
        if (taskType.equalsIgnoreCase(WeComMassTaskTypeEnum.MOMENT.name())) {
            textMessage.put("content", "【任务提醒】有新的任务啦！\n"
                    + "可前往【客户朋友圈】中确认发送，记得及时完成哦～");
        } else {
            textMessage.put("content", "【任务提醒】有新的任务啦！\n"
                    + "可前往【群发助手】中确认发送，记得及时完成哦～");
        }
        appMsgRequest.setContent(JsonUtils.toJSONString(textMessage));
        appMsgRequest.setToUser(members);
        log.info("send text massage to member for remind. request={}", appMsgRequest);
        weComSendAgentMessageRpcService.sendAgentMessage(appMsgRequest);
    }

    @Override
    public BaseResponse listMassTask(String projectId, String taskType, Integer pageNum, Integer pageSize,
                                     String corpId, List<String> statuses,
                                     String keyword, List<String> creatorIds, String sortKey,
                                     String sortOrderKey, String startTime, String endTime) {
        WeComGetMassTaskListResponse response = new WeComGetMassTaskListResponse();
        QueryMassTaskBuildSqlParam param = QueryMassTaskBuildSqlParam.builder()
                .projectUuid(projectId)
                .corpId(corpId)
                .weComMassTaskTypeEnum(taskType)
                .creatorIds(creatorIds)
                .endTime(DateUtil.parse(endTime))
                .startTime(DateUtil.parse(startTime))
                .sortKey(sortKey)
                .keyword(keyword)
                .statuses(statuses)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .sortOrderKey("DESC")
                .build();

        log.info("list weCom mass task for param. param={}", param);
        Integer count = weComMassTaskRepository.countByBuildSqlParam(param);
        log.info("count to list weCom mass task for param. count={}", count);
        List<WeComMassTaskEntity> massTaskList = weComMassTaskRepository.listByBuildSqlParam(param);
        List<WeComGetMassTaskListResponse.MassTaskDetail> massTasks = new ArrayList<>();


        massTaskList.forEach(entity -> {
            WeComGetMassTaskListResponse.MassTaskDetail detail = new WeComGetMassTaskListResponse.MassTaskDetail();
            detail.setName(entity.getName());
            detail.setCreatorName(entity.getCreatorName());
            detail.setCreatorId(entity.getCreatorId());
            detail.setId(entity.getId());
            detail.setUuid(entity.getUuid());
            detail.setScheduleTime(DateUtil.formatDateTime(entity.getScheduleTime()));
            detail.setTaskStatus(WeComMassTaskStatus.fromValue(entity.getTaskStatus()));
            detail.setScheduleType(WeComMassTaskScheduleType.fromValue(entity.getScheduleType()));
            detail.setCanRemind(canRemind(entity));
            String result = completeRateResult(entity);
            detail.setCompleteRate(result);
            detail.setCanStop(checkCanStopTask(entity));
            if (result.equals("100%")) {
                detail.setCanRemind(Boolean.FALSE);
            }

            massTasks.add(detail);
        });
        response.setTotalCount(count);
        response.setList(massTasks);
        log.info("query mass task list response. corpId={}, response={}", corpId, JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse listMembers(String projectId, String corpId, String taskType, String metricsType,
                                    Integer pageNum, Integer pageSize, String taskUuid, String keyword, String status) {
        log.info("start query mass task statistic for member. corpId={}, taskType={}, pageNum={}, pageSize={}, " +
                "taskUuid={}, status={}", corpId, taskType, pageNum, pageSize, taskUuid, status);
        if (metricsType.equals(WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue())) {
            return listMembersForExternalUser(projectId, corpId, metricsType, pageNum, pageSize, taskUuid,
                    keyword, status);
        } else if (metricsType.equals(WeComMassTaskMetricsType.MASS_TASK_COMMENTS.getValue())) {
            return listMembersForComments(projectId, corpId, metricsType, pageNum, pageSize, taskUuid,
                    keyword, status);
        }
        WeComQueryMassTaskStatisticForMembers response = new WeComQueryMassTaskStatisticForMembers();
        QueryMassTaskMemberMetricsBuildSqlParam param =
                QueryMassTaskMemberMetricsBuildSqlParam.builder().taskUuid(taskUuid).keyword(keyword).projectUuid(projectId).
                        pageNum(pageNum).pageSize(pageSize).status(status).build();
        Integer count = weComMassTaskMemberStatisticRepository.countByBuildSqlParam(param);
        log.info("query mass task member list count. count={}", count);
        List<WeComMassTaskMemberStatisticEntity> entities =
                weComMassTaskMemberStatisticRepository.listByBuildSqlParam(param);
        response.setCount(count);
        response.setStatus(status);
        List<WeComQueryMassTaskStatisticForMembers.MemberDetail> memberDetails = new ArrayList<>();
        entities.forEach(entity -> {
            WeComQueryMassTaskStatisticForMembers.MemberDetail memberDetail =
                    new WeComQueryMassTaskStatisticForMembers.MemberDetail();

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
        WeComMassTaskEntity entity = weComMassTaskRepository.findByUuid(projectId, taskType, taskUuid);
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

    private Boolean checkCanStopTask(WeComMassTaskEntity entity) {
        if (entity.getTaskStatus().equals(WeComMassTaskStatus.SENT.getValue())) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private BaseResponse listMembersForExternalUser(String projectId, String corpId, String taskType,
                                                    Integer pageNum, Integer pageSize,
                                                    String taskUuid, String keyword, String status) {
        log.info("start query mass task statistic for external user. corpId={}, taskType={}, pageNum={}, pageSize={}," +
                " taskUuid={}, status={}", corpId, taskType, pageNum, pageSize, taskUuid, status);

        WeComQueryMassTaskStatisticForMembers response = new WeComQueryMassTaskStatisticForMembers();

        QueryMassTaskMemberMetricsBuildSqlParam param =
                QueryMassTaskMemberMetricsBuildSqlParam.builder().taskUuid(taskUuid).keyword(keyword).projectUuid(projectId).
                        pageNum(pageNum).pageSize(pageSize).build();

        Integer count = weComMassTaskMemberStatisticRepository.countByBuildSqlParam(param);

        log.info("query mass task member list count. count={}", count);

        List<WeComMassTaskMemberStatisticEntity> weComMassTaskMemberStatisticEntities =
                weComMassTaskMemberStatisticRepository.listByBuildSqlParam(param);
        response.setCount(count);

        List<WeComQueryMassTaskStatisticForMembers.MemberDetail> members = new ArrayList<>();
        weComMassTaskMemberStatisticEntities.forEach(entity -> {
            WeComQueryMassTaskStatisticForMembers.MemberDetail detail =
                    new WeComQueryMassTaskStatisticForMembers.MemberDetail();
            detail.setMemberName(entity.getMemberName());
            detail.setMemberId(entity.getMemberId());
            if (status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue())) {
                detail.setExternalUserCount(entity.getExternalUserCount() == null ? 0 : entity.getExternalUserCount());
            } else if (status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.UNFRIEND.getValue())) {
                detail.setExternalUserCount(entity.getNonFriendCount() == null ? 0 : entity.getNonFriendCount());
            } else if (status.equalsIgnoreCase(WeComMassTaskExternalUserStatusEnum.EXCEED_LIMIT.getValue())) {
                detail.setExternalUserCount(entity.getExceedLimitCount() == null ? 0 : entity.getExceedLimitCount());
            } else {
                detail.setExternalUserCount(entity.getDeliveredCount() == null ? 0 : entity.getDeliveredCount());
            }
            members.add(detail);
        });

        response.setMembers(members);
        log.info("query mass task statistic for external user response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    private String completeRateResult(WeComMassTaskEntity entity) {
        String result = "0%";
        Integer nonSendCount = 0;
        Integer sendCount = 0;
        Integer sendFailedCount = 0;
        for (WeComMassTaskMemberStatusEnum value : WeComMassTaskMemberStatusEnum.values()) {
            int countValue = weComMassTaskMemberStatisticRepository.countByTaskUuidAAndStatus(entity.getUuid(),
                    value.getValue());
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
        log.info("complete mass task rate. sendCount={}, nonSendCount={}, sendFailedCount={}", sendCount,
                nonSendCount, sendFailedCount);
        if (nonSendCount != 0 || sendCount != 0 || sendFailedCount != 0) {
            result =
                    numberFormat.format((float) sendCount / (float) (nonSendCount + sendCount + sendFailedCount) * 100);
            result += "%";

        }
        return result;
    }

    private Boolean canRemind(WeComMassTaskEntity entity) {
        if (entity.getTaskStatus().equals(WeComMassTaskStatus.FINISHED.getValue()) ||
                entity.getTaskStatus().equals(WeComMassTaskStatus.UNSTART.getValue())) {
            return Boolean.FALSE;
        }
        String value = weComMassTaskCacheService.getCacheRemindCount(entity.getUuid());
        if (StringUtils.isNotEmpty(value) && Integer.valueOf(value) > 2) {
            return Boolean.FALSE;
        }
//        String today = DateUtil.today();
//        if (entity.getRemindTime() != null && DateUtil.formatDate(entity.getRemindTime()).equals(today)) {
//            return Boolean.FALSE;
//        }
        return Boolean.TRUE;
    }

    private BaseResponse listMembersForComments(String projectId, String corpId, String taskType,
                                                Integer pageNum, Integer pageSize,
                                                String taskUuid, String keyword, String status) {

        log.info("start query mass task statistic for comments. corpId={}, taskType={}, pageNum={}, pageSize={}, " +
                "taskUuid={}, status={}", corpId, taskType, pageNum, pageSize, taskUuid, status);

        WeComQueryMassTaskStatisticForMembers response = new WeComQueryMassTaskStatisticForMembers();

        QueryMassTaskExternalUserMetricsBuildSqlParam param =
                QueryMassTaskExternalUserMetricsBuildSqlParam.builder().projectUuid(projectId)
                        .pageNum(pageNum).pageSize(pageSize).keyword(keyword).taskUuid(taskUuid).status(status).build();

        Integer count = weComMassTaskExternalUserStatisticRepository.countByBuildSqlParam(param);

        List<WeComMassTaskExternalUserStatisticEntity> entities =
                weComMassTaskExternalUserStatisticRepository.listByBuildSqlParam(param);
        log.info("query mass task statistic for comments from db. status={}, entities={}", status,
                JsonUtils.toJSONString(entities));
        response.setCount(count);
        List<WeComQueryMassTaskStatisticForMembers.CommentsDetail> commentsDetails = new ArrayList<>();
        for (WeComMassTaskExternalUserStatisticEntity entity : entities) {
            WeComQueryMassTaskStatisticForMembers.CommentsDetail detail =
                    new WeComQueryMassTaskStatisticForMembers.CommentsDetail();
            detail.setUserId(entity.getExternalUserId());
            detail.setUserName(entity.getExternalUserName());
            detail.setMemberId(entity.getMemberId());
            String memberName = weComMemberMessageRepository.queryNameByMemberId(corpId, entity.getMemberId());
            detail.setMemberName(StringUtils.isBlank(memberName) ? entity.getMemberId() : memberName);
            detail.setAddCommentsTime(DateUtil.formatDateTime(entity.getAddCommentsTime()));
            commentsDetails.add(detail);
        }

        response.setComments(commentsDetails);
        log.info("query mass task statistic for comments response. corpId={}, response={}", corpId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getMassTaskDetails(String projectUuid, Integer taskId) {
        WeComMassTaskDetailResponse response = new WeComMassTaskDetailResponse();

        WeComMassTaskEntity entity = weComMassTaskRepository.queryById(taskId);
        if (entity == null) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_IS_EMPTY);
        }
        BeanUtils.copyProperties(entity, response);
        response.setScheduleTime(DateUtil.formatDateTime(entity.getScheduleTime()));
        response.setCreateTime(DateUtil.formatDateTime(entity.getCreateTime()));
        List<WeComMassTaskSendMsg> list = JsonUtils.toArray(entity.getContent(), WeComMassTaskSendMsg.class);
        response.setMsgContent(list);
        log.info("query mass task detail response. projectUuid = {}, taskId={}, response={}", projectUuid, taskId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getMassTaskStatistic(String taskUuid, String metricsType) {
        WeComQueryMassTaskStatisticResponse response = new WeComQueryMassTaskStatisticResponse();

        if (WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue().equals(metricsType)) {
            WeComQueryMassTaskStatisticResponse.ExternalUserStatisticDetail externalUserDetail =
                    new WeComQueryMassTaskStatisticResponse.ExternalUserStatisticDetail();

            for (WeComMassTaskExternalUserStatusEnum value : WeComMassTaskExternalUserStatusEnum.values()) {
                int count = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid,
                        value.getValue());
                if (value == WeComMassTaskExternalUserStatusEnum.UNDELIVERED) {
                    externalUserDetail.setNonDeliveredCount(count);
                } else if (value == WeComMassTaskExternalUserStatusEnum.DELIVERED) {
                    externalUserDetail.setDeliveredCount(count);
                } else if (value == WeComMassTaskExternalUserStatusEnum.UNFRIEND) {
                    externalUserDetail.setUnfriendCount(count);
                } else if (value == WeComMassTaskExternalUserStatusEnum.EXCEED_LIMIT) {
                    externalUserDetail.setReceiveLimitCount(count);
                }
            }
            externalUserDetail.setExternalUserTotalCount(externalUserDetail.getNonDeliveredCount() +
                    externalUserDetail.getDeliveredCount() + externalUserDetail.getReceiveLimitCount() + externalUserDetail.getUnfriendCount());
            response.setExternalUserDetail(externalUserDetail);
        } else if (WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue().equals(metricsType)) {
            WeComQueryMassTaskStatisticResponse.MemberStatisticDetail memberDetail =
                    new WeComQueryMassTaskStatisticResponse.MemberStatisticDetail();

            for (WeComMassTaskMemberStatusEnum value : WeComMassTaskMemberStatusEnum.values()) {
                int count = weComMassTaskMemberStatisticRepository.countByTaskUuidAAndStatus(taskUuid,
                        value.getValue());
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
        } else if (WeComMassTaskMetricsType.MASS_TASK_COMMENTS.getValue().equals(metricsType)) {
            WeComQueryMassTaskStatisticResponse.CommentsStatisticDetail commentsDetail =
                    new WeComQueryMassTaskStatisticResponse.CommentsStatisticDetail();

            Integer commentCount =
                    weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatusAndType(taskUuid,
                            WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue(),
                            WeComMassTaskExternalUserStatusEnum.COMMENT.name());

            Integer commentCountForMember =
                    weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatusAndType(taskUuid,
                            WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue(),
                            WeComMassTaskExternalUserStatusEnum.COMMENT.name());

            Integer likeCount = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatusAndType(taskUuid,
                    WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue(),
                    WeComMassTaskExternalUserStatusEnum.LIKE.name());

            Integer likeCountForMember =
                    weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatusAndType(taskUuid,
                            WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue(),
                            WeComMassTaskExternalUserStatusEnum.LIKE.name());

            commentsDetail.setExternalUserLikeCount(likeCount);
            commentsDetail.setExternalUserCommentsCount(commentCount);
            commentsDetail.setMemberCommentsCount(commentCountForMember);
            commentsDetail.setMemberLikeCount(likeCountForMember);
            response.setCommentsDetail(commentsDetail);
        } else {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_METRICS_TYPE_NOT_SUPPORT);
        }
        log.info("query mass task statistic response. response={}", JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getMassTaskCreators(String projectUuid, String corpId, String taskType) {
        log.info("start to query weCom mass task creator list, projectUuid={}, taskType={}, corpId={}",
                projectUuid, taskType, corpId);
        WeComMassTaskCreatorsResponse response = new WeComMassTaskCreatorsResponse();
        List<WeComMassTaskCreatorsResponse.CreatorMessage> creatorList = new ArrayList<>();
        List<WeComMassTaskCreators> creators = weComMassTaskRepository.listCreatorsByTaskType(projectUuid, corpId,
                taskType);
        log.info("query weCom mass task creator list from db, creators={}", creators);
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
        log.info("query mass task creator list response. response={}", JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse deleteMassTask(String taskType, String taskUuid) {
        WeComMassTaskEntity entity = weComMassTaskRepository.getByTaskUUID(taskUuid);
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
        weComMassTaskRepository.deleteByIdAndTaskType(entity.getId(), taskType);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse checkMassTaskName(String projectId, String taskType, Integer taskId, String name) {
        try {
            log.info("Begin to check weCom mass task cname, projectUuid={}, taskType={}, taskId={}, taskCname={}.",
                    projectId, taskType, taskId, name);
            if (!WeComMassTaskTypeEnum.isSupported(taskType)) {
                log.error("Unsupported wecom mass task type, taskType: {}.", taskType);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_NOT_SUPPORT_MASS_TASK.getCode()).message(ErrorCodeEnum.ERROR_NOT_SUPPORT_MASS_TASK.getMessage()).build();
            }
            WeComMassTaskEntity weComMassTaskEntity =
                    weComMassTaskRepository.getMassTaskByMassTaskTypeAndName(projectId, taskType, name);
            // 仅当「非同一个计划且同名」的情况，认为重名
            if (weComMassTaskEntity != null && !weComMassTaskEntity.getId().equals(taskId)) {
                log.info("failed to check weCom mass task name, projectUuid={}, taskType={}, taskId={}, name={}, " +
                        "weComMassTaskEntity={}.", projectId, taskType, taskId, name, weComMassTaskEntity);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getCode()).message(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getMessage()).build();
            }
            log.info("Succeed to check weCom mass task name, projectUuid={}, taskType={}, taskId={}, taskCname={}, " +
                    "weComMassTaskEntity={}.", projectId, taskType, taskId, name, weComMassTaskEntity);
            return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
        } catch (Exception e) {
            log.error("Failed to check weCom mass task cname, projectUuid={}, taskType={}, taskId={}, name={}.",
                    projectId, taskType, taskId, name, e);
        }
        return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getCode()).message(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getMessage()).build();
    }

    @Override
    public BaseResponse stopMassTaskMessage(String projectId, String corpId, String taskType, String taskUuid) {
        WeComMassTaskEntity entity = weComMassTaskRepository.findByUuid(projectId, taskType, taskUuid);
        if (entity == null) {
            return BaseResponse.failure(ERROR_WEB_MASS_TASK_IS_EMPTY);
        }

        WeComAgentMessageEntity agentMessageEntity = weComAgentMessageRepository.getWeComAgentByCorp(projectId, corpId);
        log.info("mass task to remind send. weComMassTask={}", entity);
        String agentId = (agentMessageEntity != null) ? agentMessageEntity.getAgentId() : "";
        int offset = 0;
        int limitSize = 1000;
        do {

            log.info("start query mass task list.taskUuid={}, offset={}, limitSize={}", entity.getUuid(), offset,
                    limitSize);
            List<WeComMassTaskSyncStatisticEntity> messages =
                    weComMassTaskSyncStatisticRepository.getWeComMassTaskResponseByTaskUuid(taskUuid, offset,
                            limitSize);
            if (CollectionUtils.isEmpty(messages)) {
                log.info("not stop mass task.weComMassTask={}", entity);
                return BaseResponse.success();
            }

            for (WeComMassTaskSyncStatisticEntity item : messages) {
                if (taskType.equals(WeComMassTaskTypeEnum.MOMENT.name())) {
                    WeComStopMomentMassTaskClientRequest request = new WeComStopMomentMassTaskClientRequest();
                    request.setAgentId(agentId);
                    request.setCorpId(corpId);
                    request.setMomentId(item.getSendId());
                    if (item.getSendIdType().equals(WeComMassTaskSendIdType.JOB_ID.name())) {
                        WeComMomentMassTaskCreateResultClientRequest
                                weComMomentMassTaskCreateResultClientRequest =
                                new WeComMomentMassTaskCreateResultClientRequest();
                        weComMomentMassTaskCreateResultClientRequest.setCorpId(corpId);
                        weComMomentMassTaskCreateResultClientRequest.setAgentId(agentId);
                        weComMomentMassTaskCreateResultClientRequest.setJobid(item.getSendId());
                        RpcResponse<WeComMassTaskForMomentCreateResponse> response =
                                weComMassTaskRpcService.queryMomentMassTaskForCreateResult(weComMomentMassTaskCreateResultClientRequest);
                        log.info("query mass task from moment create result. jobId={}, response={}", item.getSendId()
                                , response);
                        if (response.getSuccess()) {
                            WeComMassTaskForMomentCreateResponse weComMassTaskForMomentCreateResponse =
                                    response.getData();
                            if (weComMassTaskForMomentCreateResponse.getStatus() == CREATE_MOMENT_STATUE_COMPLETE &&
                                    weComMassTaskForMomentCreateResponse.getResult().getCode().equals(ErrorCodeEnum.OK.getCode())) {
                                String momentId = weComMassTaskForMomentCreateResponse.getResult().getMomentId();
                                request.setMomentId(momentId);
                            }
                        }
                    }
                    weComMassTaskRpcService.stopMomentMassTask(request);
                } else {
                    WeComRemindMemberMessageClientRequest request = new WeComRemindMemberMessageClientRequest();
                    request.setAgentId(agentId);
                    request.setCorpId(corpId);
                    request.setMsgId(item.getSendId());
                    weComMassTaskRpcService.stopMassTask(request);
                }
            }
            offset = (messages.size() < limitSize) ? 0 : (offset + limitSize);
            log.info("query mass task is empty.taskUuid={}", taskUuid);
        } while (offset > 0);

        weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                WeComMassTaskStatus.FINISHED.getValue());
        return BaseResponse.success();
    }
}
