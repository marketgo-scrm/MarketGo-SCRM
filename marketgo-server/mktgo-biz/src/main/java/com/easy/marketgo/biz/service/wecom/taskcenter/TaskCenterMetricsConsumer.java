package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterExternalUserStatisticEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberStatisticEntity;
import com.easy.marketgo.core.model.bo.WeComSendMassTaskContent;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterMetrics;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_EXTERNAL_USER_DETAIL;
import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 3:28 PM
 * Describe:
 */
@Slf4j
@Component
public class TaskCenterMetricsConsumer {

    @Autowired
    private WeComTaskCenterMemberStatisticRepository weComTaskCenterMemberStatisticRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComTaskCenterExternalUserStatisticRepository weComTaskCenterExternalUserStatisticRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_TASK_CENTER_STATISTIC}, containerFactory =
            "weComTaskCenterStatisticListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer task center metrics message. message={}", data);
        WeComTaskCenterMetrics weComTaskCenterMetrics = JsonUtils.toObject(data, WeComTaskCenterMetrics.class);
        WeComMassTaskMetricTypeEnum typeEnum = weComTaskCenterMetrics.getMetricType();

        if (typeEnum == MASS_TASK_MEMBER_DETAIL) {
            changeMemberStatus(weComTaskCenterMetrics.getProjectUuid(), weComTaskCenterMetrics.getCorpId(),
                    weComTaskCenterMetrics.getUuid(), weComTaskCenterMetrics.getTaskUuid(),
                    DateUtil.parse(weComTaskCenterMetrics.getPlanTime()), weComTaskCenterMetrics.getMemberMessage());
        } else if (typeEnum == MASS_TASK_EXTERNAL_USER_DETAIL) {
            changeExternalUserStatus(weComTaskCenterMetrics.getProjectUuid(), weComTaskCenterMetrics.getCorpId(),
                    weComTaskCenterMetrics.getTaskType(), weComTaskCenterMetrics.getUuid(),
                    weComTaskCenterMetrics.getTaskUuid(), DateUtil.parse(weComTaskCenterMetrics.getPlanTime()),
                    weComTaskCenterMetrics.getExternalUserMessage());
        }
    }

    private void changeMemberStatus(final String projectUuid, final String corpId, final String uuid,
                                    final String taskUuid, Date planTime,
                                    WeComTaskCenterMetrics.MemberMessage message) {
        if (CollectionUtils.isEmpty(message.getMemberState())) {
            log.info("failed to change task center for member status because member list is empty");
            return;
        }
        log.info("start change member status for task center. projectUuid={}, corpId={}, taskUuid={}, message={}",
                projectUuid, corpId, taskUuid, message);
        for (WeComTaskCenterMetrics.MemberStatus item : message.getMemberState()) {
            try {
                if (item.getStatus() != WeComMassTaskMemberStatusEnum.UNSENT) {
                    weComTaskCenterMemberStatisticRepository.updateMemberStatusBytaskUuid(
                            WeComMassTaskMemberStatusEnum.UNSENT.getValue(), item.getStatus().getValue(),
                            item.getTime(), taskUuid, uuid, item.getMemberId());
                } else {
                    String memberName = weComMemberMessageRepository.queryNameByMemberId(corpId, item.getMemberId());
                    WeComTaskCenterMemberStatisticEntity entity = new WeComTaskCenterMemberStatisticEntity();
                    entity.setMemberId(item.getMemberId());
                    entity.setProjectUuid(projectUuid);
                    entity.setUuid(uuid);
                    entity.setTaskUuid(taskUuid);
                    entity.setPlanTime(planTime);
                    entity.setMemberName(memberName);
                    entity.setStatus(item.getStatus().name());
                    entity.setExternalUserCount(item.getExternalUserCount());
                    log.info("save change member status for task center. projectUuid={}, corpId={}, taskUuid={}, " +
                                    "message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComTaskCenterMemberStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change member status for task center. projectUuid={}, corpId={}, taskUuid={}, " +
                                "message={}",
                        projectUuid, corpId, taskUuid, message, e);
            }
        }
    }

    private void changeExternalUserStatus(final String projectUuid, final String corpId,
                                          WeComMassTaskTypeEnum taskType, final String uuid, final String taskUuid,
                                          Date planTime, WeComTaskCenterMetrics.ExternalUserMessage message) {
        if (CollectionUtils.isEmpty(message.getExternalUserStatus())) {
            log.info("failed to change task center for external user status because member list is empty");
            return;
        }
        log.info("start to change external user status for task center. projectUuid={}, corpId={}, taskUuid={}, " +
                "message={}", projectUuid, corpId, taskUuid, message);
        for (WeComTaskCenterMetrics.ExternalUserStatus item : message.getExternalUserStatus()) {
            try {
                if (item.getStatus() != WeComMassTaskExternalUserStatusEnum.UNDELIVERED) {

                    weComTaskCenterExternalUserStatisticRepository.updateExternalUserStatus(item.getStatus().name(),
                            item.getTime(), uuid, taskUuid, message.getMemberId(), item.getExternalUserId());
                    updateExternalMetricsCount(message.getMemberId(), taskUuid, uuid);
                    updateTaskCenterStatus(taskUuid);
                    updateExternalMetricsCount(message.getMemberId(), taskUuid, uuid);
                } else {
                    WeComTaskCenterExternalUserStatisticEntity entity =
                            new WeComTaskCenterExternalUserStatisticEntity();
                    entity.setMemberId(message.getMemberId());
                    entity.setProjectUuid(projectUuid);
                    entity.setTaskUuid(taskUuid);
                    entity.setUuid(uuid);
                    entity.setPlanTime(planTime);
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

                    entity.setReceiveTime(DateUtil.parse(item.getTime()));
                    log.info("save change external user status for task center. projectUuid={}, corpId={}, " +
                                    "taskUuid={}, message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComTaskCenterExternalUserStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change external user status for task center. projectUuid={}, corpId={}, " +
                        "taskUuid={}, message={}", projectUuid, corpId, taskUuid, message, e);
            }
        }
    }

    private void updateExternalMetricsCount(String memberId, String taskUuid, String uuid) {
        Integer deliveredCount = weComTaskCenterExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid, uuid,
                WeComMassTaskExternalUserStatusEnum.DELIVERED.getValue());

        Integer unFriendCount = weComTaskCenterExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid, uuid,
                WeComMassTaskExternalUserStatusEnum.UNFRIEND.getValue());

        log.info("query external user metrics count for task center. deliveredCount={}, unFriendCount={}",
                deliveredCount,
                unFriendCount);

        weComTaskCenterMemberStatisticRepository.updateMemberMetricsForExternalUser(deliveredCount == null ? 0 :
                deliveredCount, unFriendCount == null ? 0 : unFriendCount, taskUuid, memberId);
    }

    private void updateTaskCenterStatus(String taskUuid) {
        WeComTaskCenterEntity entity = weComTaskCenterRepository.getByTaskUUID(taskUuid);
        if (entity == null) {
            log.info("failed to query task center. taskUuid={}", taskUuid);
            return;
        }
        if (!entity.getScheduleType().equalsIgnoreCase(WeComMassTaskScheduleType.FIXED_TIME.getValue())) {
            changeTaskCenterFinish(entity);
        } else {
            if (entity.getFinishTime() != null && entity.getFinishTime().getTime() < DateUtil.date().getTime()) {
                changeTaskCenterFinish(entity);
            }
        }
    }

    private void changeTaskCenterFinish(WeComTaskCenterEntity entity) {
        Integer unsentCount = weComTaskCenterMemberStatisticRepository.countByTaskUuidAndTaskStatus(entity.getUuid(),
                WeComMassTaskMemberStatusEnum.UNSENT.getValue());
        if (unsentCount == null || (unsentCount != null && unsentCount > 0)) {
            return;
        }
        List<String> mediaUuids = getMediaUuid(entity.getContent());
        if (CollectionUtils.isNotEmpty(mediaUuids)) {
            weComMediaResourceRepository.updateMediaByUuid(mediaUuids);
        }
        weComTaskCenterRepository.updateTaskStatusAndFinishTime(entity.getUuid(),
                WeComMassTaskStatus.FINISHED.getValue()
                , entity.getFinishTime() == null ? DateUtil.date() : entity.getFinishTime());
    }

    private List<String> getMediaUuid(String content) {
        List<String> mediaUuids = new ArrayList<>();

        List<WeComSendMassTaskContent> weComSendMassTaskContents = JsonUtils.toArray(content,
                WeComSendMassTaskContent.class);

        weComSendMassTaskContents.forEach(weComSendMassTaskContent -> {
            if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                if (weComSendMassTaskContent.getImage() != null &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getImage().getMediaUuid())) {
                    mediaUuids.add(weComSendMassTaskContent.getImage().getMediaUuid());
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {

                if (weComSendMassTaskContent.getLink() != null &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getMediaUuid())) {
                    mediaUuids.add(weComSendMassTaskContent.getLink().getMediaUuid());
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.MINIPROGRAM) {
                if (weComSendMassTaskContent.getMiniProgram() != null) {
                    mediaUuids.add(weComSendMassTaskContent.getMiniProgram().getMediaUuid());
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.VIDEO) {
                mediaUuids.add(weComSendMassTaskContent.getVideo().getMediaUuid());
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.FILE) {
                mediaUuids.add(weComSendMassTaskContent.getFile().getMediaUuid());
            }
        });

        return mediaUuids;
    }
}
