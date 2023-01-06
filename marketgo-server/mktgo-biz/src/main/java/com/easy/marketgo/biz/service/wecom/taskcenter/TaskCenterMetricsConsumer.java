package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterExternalUserStatisticEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberStatisticEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterMetrics;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberStatisticRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
        log.info("start change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                projectUuid, corpId, taskUuid, message);
        for (WeComTaskCenterMetrics.MemberStatus item : message.getMemberState()) {
            try {
                if (!item.getStatus().equals(WeComMassTaskMemberStatusEnum.UNSENT)) {
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
                    log.info("save change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComTaskCenterMemberStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change member status. projectUuid={}, corpId={}, taskUuid={}, message={}",
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
                    entity.setStatus(item.getStatus().name());
                    entity.setReceiveTime(DateUtil.parse(item.getTime()));
                    log.info("save change external user status. projectUuid={}, corpId={}, taskUuid={}, message={}",
                            projectUuid, corpId, taskUuid, entity);
                    weComTaskCenterExternalUserStatisticRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("failed to change external user status for task center. projectUuid={}, corpId={}, " +
                        "taskUuid={}, message={}", projectUuid, corpId, taskUuid, message, e);
            }
        }
    }

    private void updateMassTaskExternalMetricsCount(String memberId, String taskUuid, String uuid) {
        Integer deliveredCount = weComTaskCenterExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid, uuid,
                WeComMassTaskExternalUserStatusEnum.DELIVERED.getValue());

        Integer unFriendCount = weComTaskCenterExternalUserStatisticRepository.countByTaskUuidAndStatus(taskUuid, uuid,
                WeComMassTaskExternalUserStatusEnum.UNFRIEND.getValue());

        log.info("query external user metrics count. deliveredCount={}, unFriendCount={}", deliveredCount,
                unFriendCount);

        weComTaskCenterMemberStatisticRepository.updateMemberMetricsForExternalUser(deliveredCount == null ? 0 :
                deliveredCount, unFriendCount == null ? 0 : unFriendCount, taskUuid, memberId);
    }
}
