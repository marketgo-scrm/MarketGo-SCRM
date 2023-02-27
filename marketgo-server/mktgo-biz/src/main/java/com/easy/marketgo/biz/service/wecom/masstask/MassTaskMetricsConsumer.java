package com.easy.marketgo.biz.service.wecom.masstask;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskExternalUserStatisticEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskMemberStatisticEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.model.bo.WeComMassTaskMetricsBO;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSyncStatisticRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskMemberStatisticRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 3:28 PM
 * Describe:
 */
@Slf4j
@Component
public class MassTaskMetricsConsumer {

    @Autowired
    private WeComMassTaskSyncStatisticRepository weComMassTaskSyncStatisticRepository;

    @Autowired
    private WeComMassTaskMemberStatisticRepository weComMassTaskMemberStatisticRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComMassTaskExternalUserStatisticRepository weComMassTaskExternalUserStatisticRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MASS_TASK_STATISTIC}, containerFactory =
            "weComMassTaskStatisticListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer mass task metrics message. message={}", data);
        WeComMassTaskMetricsBO weComMassTaskMetrics = JsonUtils.toObject(data, WeComMassTaskMetricsBO.class);
        WeComMassTaskMetricTypeEnum typeEnum = weComMassTaskMetrics.getMetricType();
//        if (typeEnum == MOMENT_TASK_CREATE_DETAIL) {
//            createMomentMassTask(weComMassTaskMetrics.getTaskUuid(), weComMassTaskMetrics.getCreateMomentMessage());
//        } else if (typeEnum == MOMENT_TASK_PUBLISH_DETAIL) {
//            publishMomentMassTask(weComMassTaskMetrics.getProjectUuid(), weComMassTaskMetrics.getTaskUuid(),
//                    weComMassTaskMetrics.getCreateMomentMessage());
//        } else
        if (typeEnum == MOMENT_TASK_COMMENTS_DETAIL) {
            momentMassTaskForComments(weComMassTaskMetrics.getProjectUuid(),
                    weComMassTaskMetrics.getCorpId(), weComMassTaskMetrics.getTaskUuid(),
                    weComMassTaskMetrics.getMomentCommentsMessage());
        } else if (typeEnum == MASS_TASK_MEMBER_DETAIL) {
            changeMemberStatus(weComMassTaskMetrics.getProjectUuid(), weComMassTaskMetrics.getCorpId(),
                    weComMassTaskMetrics.getTaskUuid(), weComMassTaskMetrics.getMemberMessage());
        } else if (typeEnum == MASS_TASK_EXTERNAL_USER_DETAIL) {
            changeExternalUserStatus(weComMassTaskMetrics.getProjectUuid(), weComMassTaskMetrics.getCorpId(),
                    weComMassTaskMetrics.getTaskType(), weComMassTaskMetrics.getTaskUuid(),
                    weComMassTaskMetrics.getExternalUserMessage());
        }
    }

    private void momentMassTaskForComments(final String projectUuid, final String corpId, final String taskUuid,
                                           WeComMassTaskMetricsBO.MomentCommentsMessage message) {

        log.info("moment mass task comments message. taskUuid={}, message={}", taskUuid, message);
        weComMassTaskExternalUserStatisticRepository.deleteByTaskUuidAndMemberId(taskUuid, message.getMemberId(),
                WeComMassTaskExternalUserStatusEnum.COMMENT.getValue());
        weComMassTaskExternalUserStatisticRepository.deleteByTaskUuidAndMemberId(taskUuid, message.getMemberId(),
                WeComMassTaskExternalUserStatusEnum.LIKE.getValue());
        List<WeComMassTaskExternalUserStatisticEntity> entities = new ArrayList<>();
        for (WeComMassTaskMetricsBO.ExternalUserStatus item : message.getExternalUserStatus()) {
            try {
                WeComMassTaskExternalUserStatisticEntity entity = new WeComMassTaskExternalUserStatisticEntity();
                entity.setMemberId(message.getMemberId());
                entity.setProjectUuid(projectUuid);
                entity.setTaskUuid(taskUuid);
                entity.setExternalUserId(item.getExternalUserId());
                if (item.getType().equals(WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue())) {
                    WeComRelationMemberExternalUserEntity externalUserEntity =
                            weComRelationMemberExternalUserRepository.queryByExternalUser(corpId,
                                    item.getExternalUserId());
                    if (externalUserEntity != null) {
                        entity.setExternalUserName(externalUserEntity.getExternalUserName());
                    }
                } else {
                    String memberName = weComMemberMessageRepository.queryNameByMemberId(corpId,
                            item.getExternalUserId());
                    if (StringUtils.isNotEmpty(memberName)) {
                        entity.setExternalUserName(memberName);
                    }
                }
                entity.setStatus(item.getStatus().name());
                entity.setExternalUserType(item.getType());
                entity.setAddCommentsTime(DateUtil.date(Long.valueOf(item.getTime())));
                log.info("save external user status commits message. projectUuid={}, corpId={}, taskUuid={}, " +
                                "message={}",
                        projectUuid, corpId, taskUuid, entity);
                entities.add(entity);

            } catch (Exception e) {
                log.error("failed to change external user status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                        projectUuid, corpId, taskUuid, message, e);
            }
        }
        weComMassTaskExternalUserStatisticRepository.saveAll(entities);

    }

//    private void createMomentMassTask(final String taskUuid, WeComMassTaskMetricsBO.CreateMomentMessage message) {
//        log.info("create moment mass task message. taskUuid={}, message={}", taskUuid, message);
//        WeComTaskCenterSyncStatisticEntity entity = new WeComTaskCenterSyncStatisticEntity();
//        entity.setMediaUuid(UuidUtils.generateUuid());
//        entity.setTaskUuid(taskUuid);
//        entity.setSendIdType(WeComMassTaskSendIdType.JOB_ID);
//        entity.setTaskType(WeComMassTaskTypeEnum.MOMENT);
//        entity.setSendId(message.getJobId());
//        entity.setDeleted(Boolean.FALSE);
//        weComMassTaskSyncStatisticRepository.save(entity);
//    }

//    private void initPublishMomentMemberStatus(final String projectUuid, final String taskUuid,
//                                               WeComMassTaskMetricsBO.CreateMomentMessage message) {
//        if (CollectionUtils.isEmpty(message.getMemberState())) {
//            log.info("failed to init create moment mass task for member status because member list is empty");
//            return;
//        }
//        log.info("init publish member status for moment mass task message. taskUuid={}, message={}", taskUuid,
//                message);
//        List<WeComTaskCenterMemberStatisticEntity> entityList = new ArrayList<>();
//        for (WeComMassTaskMetricsBO.MemberStatus item : message.getMemberState()) {
//            WeComTaskCenterMemberStatisticEntity entity = new WeComTaskCenterMemberStatisticEntity();
//
//            entity.setMemberId(item.getMemberId());
//            entity.setProjectUuid(projectUuid);
//            entity.setTaskUuid(taskUuid);
//            entity.setSendId(message.getJobId());
//            entity.setStatus(item.getStatus().name());
//            entityList.add(entity);
//        }
//        weComMassTaskMemberStatisticRepository.saveAll(entityList);
//    }

//    private void publishMomentMassTask(final String projectId, final String taskUuid,
//                                       WeComMassTaskMetricsBO.CreateMomentMessage message) {
//        log.info("create moment mass task message. taskUuid={}, message={}", taskUuid, message);
////        weComMassTaskSyncStatisticRepository.updateSendIdMsg(message.getJobId(), message.getMomentId(),
////                WeComMassTaskSendIdType.MOMENT_ID.name());
////        initPublishMomentMemberStatus(projectId, taskUuid, message);
//    }

    private void changeMemberStatus(final String projectUuid, final String corpId, final String taskUuid,
                                    WeComMassTaskMetricsBO.MemberMessage message) {
        if (CollectionUtils.isEmpty(message.getMemberState())) {
            log.info("failed to change mass task for member status because member list is empty");
            return;
        }
        log.info("start change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                projectUuid, corpId, taskUuid, message);
        for (WeComMassTaskMetricsBO.MemberStatus item : message.getMemberState()) {
            try {
                if (!item.getStatus().equals(WeComMassTaskMemberStatusEnum.UNSENT)) {
                    WeComMassTaskMemberStatisticEntity memberStatus =
                            weComMassTaskMemberStatisticRepository.queryByMemberAndTaskUuid(taskUuid,
                                    item.getMemberId());
                    String sendIds = memberStatus.getSendId();
                    List<String> sendIdList = Arrays.asList(sendIds.split(","));
                    if (sendIdList.size() > 1) {
                        sendIdList.remove(message.getMsgId());
                        sendIds = sendIdList.stream().collect(Collectors.joining(","));
                    }
                    weComMassTaskMemberStatisticRepository.updateMemberStatus(sendIds,
                            item.getStatus().name(), taskUuid, item.getMemberId());
                } else {
                    String memberName = weComMemberMessageRepository.queryNameByMemberId(corpId, item.getMemberId());
                    WeComMassTaskMemberStatisticEntity entity = new WeComMassTaskMemberStatisticEntity();
                    entity.setMemberId(item.getMemberId());
                    entity.setProjectUuid(projectUuid);
                    entity.setTaskUuid(taskUuid);
                    entity.setSendId(message.getMsgId());
                    entity.setMemberName(memberName);
                    entity.setStatus(item.getStatus().name());
                    entity.setExternalUserCount(item.getExternalUserCount());
                    log.info("save change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComMassTaskMemberStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                        projectUuid, corpId, taskUuid, message, e);
            }
        }
    }

    private void changeExternalUserStatus(final String projectUuid, final String corpId,
                                          WeComMassTaskTypeEnum taskType, final String taskUuid,
                                          WeComMassTaskMetricsBO.ExternalUserMessage message) {
        if (CollectionUtils.isEmpty(message.getExternalUserStatus())) {
            log.info("failed to change mass task for external user status because member list is empty");
            return;
        }
        log.info("start change change external user status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                projectUuid, corpId, taskUuid, message);
        for (WeComMassTaskMetricsBO.ExternalUserStatus item : message.getExternalUserStatus()) {
            try {
                if (item.getStatus() != WeComMassTaskExternalUserStatusEnum.UNRECOGNIZED && item.getStatus() != WeComMassTaskExternalUserStatusEnum.UNDELIVERED) {
                    int count = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndMemberId(taskUuid,
                            message.getMemberId(), item.getExternalUserId());
                    if (count > 0) {
                        weComMassTaskExternalUserStatisticRepository.updateExternalUserStatus(item.getStatus().name(),
                                taskUuid, message.getMemberId(), item.getExternalUserId());
                    } else {
                        WeComMassTaskExternalUserStatisticEntity entity =
                                new WeComMassTaskExternalUserStatisticEntity();
                        entity.setMemberId(message.getMemberId());
                        entity.setProjectUuid(projectUuid);
                        entity.setTaskUuid(taskUuid);
                        entity.setExternalUserId(item.getExternalUserId());
                        if (taskType == WeComMassTaskTypeEnum.GROUP) {
                            WeComGroupChatsEntity weComGroupChatsEntity =
                                    weComGroupChatsRepository.queryByChatId(corpId,
                                            item.getExternalUserId());
                            if (weComGroupChatsEntity != null) {
                                entity.setExternalUserName(weComGroupChatsEntity.getGroupChatName());
                            }
                        }
                        entity.setStatus(item.getStatus().name());
                        log.info("save change group chats status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                                projectUuid, corpId, taskUuid, entity);
                        weComMassTaskExternalUserStatisticRepository.save(entity);
                    }
                } else {
                    WeComMassTaskExternalUserStatisticEntity entity = new WeComMassTaskExternalUserStatisticEntity();
                    entity.setMemberId(message.getMemberId());
                    entity.setProjectUuid(projectUuid);
                    entity.setTaskUuid(taskUuid);
                    entity.setExternalUserId(item.getExternalUserId());
                    if (taskType == WeComMassTaskTypeEnum.SINGLE) {
                        WeComRelationMemberExternalUserEntity externalUserEntity =
                                weComRelationMemberExternalUserRepository.queryByExternalUser(corpId,
                                        item.getExternalUserId());
                        if (externalUserEntity != null) {
                            entity.setExternalUserName(externalUserEntity.getExternalUserName());
                        }
                    } else if (taskType == WeComMassTaskTypeEnum.GROUP) {
                        WeComGroupChatsEntity weComGroupChatsEntity = weComGroupChatsRepository.queryByChatId(corpId,
                                item.getExternalUserId());
                        if (weComGroupChatsEntity != null) {
                            entity.setExternalUserName(weComGroupChatsEntity.getGroupChatName());
                        }
                    }
                    entity.setStatus(item.getStatus().name());
                    log.info("save change external user status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComMassTaskExternalUserStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change external user status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                        projectUuid, corpId, taskUuid, message, e);
            }
        }
        if (message.getFinish().equals(Boolean.TRUE)) {
            updateMassTaskExternalMetricsCount(message.getMemberId(), taskUuid);
            weComMassTaskSyncStatisticRepository.deleteByTaskUuidAndSendId(taskUuid, message.getMsgId());
        }
    }

    private void updateMassTaskExternalMetricsCount(String memberId, String taskUuid) {
        Integer deliveredCount = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid,
                WeComMassTaskExternalUserStatusEnum.DELIVERED.getValue());

        Integer unFriendCount = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid,
                WeComMassTaskExternalUserStatusEnum.UNFRIEND.getValue());

        Integer limitCount = weComMassTaskExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid,
                WeComMassTaskExternalUserStatusEnum.EXCEED_LIMIT.getValue());

        log.info("query external user metrics count. deliveredCount={}, unFriendCount={}, limitCount={}",
                deliveredCount, unFriendCount, limitCount);

        weComMassTaskMemberStatisticRepository.updateMemberMetricsForExternalUser(deliveredCount == null ? 0 :
                        deliveredCount, unFriendCount == null ? 0 : unFriendCount, limitCount == null ? 0 : limitCount,
                taskUuid, memberId);
    }
}
