package com.easy.marketgo.core.service.taskcenter;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterMetrics;
import com.easy.marketgo.core.service.WeComAgentMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_EXTERNAL_USER_DETAIL;
import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/12/22 10:44 AM
 * Describe:
 */
@Slf4j
@Component
public class SendTaskCenterBaseConsumer {

    @Autowired
    private RabbitTemplate weComTaskCenterStatisticAmqpTemplate;

    @Resource
    private WeComAgentMessageService weComAgentMessageService;

    protected void produceRabbitMqMessage(Object values) {
        weComTaskCenterStatisticAmqpTemplate.convertAndSend(RabbitMqConstants.EXCHANGE_NAME_WECOM_TASK_CENTER_STATISTIC,
                RabbitMqConstants.ROUTING_KEY_WECOM_TASK_CENTER_STATISTIC, values);
    }

    public void sendExternalUserStatusDetail(String projectUuid, String corpId, WeComMassTaskTypeEnum taskType,
                                             String taskUuid, String memberId, String uuid,
                                             List<String> externalUsers, String planTime, String sentTime,
                                             WeComMassTaskExternalUserStatusEnum status, Boolean finish) {
        WeComTaskCenterMetrics weComMassTaskMetrics = new WeComTaskCenterMetrics();
        weComMassTaskMetrics.setTaskUuid(taskUuid);
        weComMassTaskMetrics.setMetricType(MASS_TASK_EXTERNAL_USER_DETAIL);
        weComMassTaskMetrics.setProjectUuid(projectUuid);
        weComMassTaskMetrics.setUuid(uuid);
        weComMassTaskMetrics.setCorpId(corpId);
        weComMassTaskMetrics.setTaskType(taskType);
        if (StringUtils.isNotBlank(planTime)) {
            weComMassTaskMetrics.setPlanTime(planTime);
        }
        WeComTaskCenterMetrics.ExternalUserMessage externalUserMessage =
                new WeComTaskCenterMetrics.ExternalUserMessage();

        List<WeComTaskCenterMetrics.ExternalUserStatus> statuses = new ArrayList<>();

        externalUsers.forEach(externalUser -> {
            WeComTaskCenterMetrics.ExternalUserStatus externalUserStatus =
                    new WeComTaskCenterMetrics.ExternalUserStatus();
            externalUserStatus.setExternalUserId(externalUser);
            externalUserStatus.setStatus(status);
            if (StringUtils.isNotBlank(sentTime)) {
                externalUserStatus.setTime(sentTime);
            }
            statuses.add(externalUserStatus);
        });

        externalUserMessage.setExternalUserStatus(statuses);
        externalUserMessage.setMemberId(memberId);
        externalUserMessage.setFinish(finish);
        weComMassTaskMetrics.setExternalUserMessage(externalUserMessage);
        produceRabbitMqMessage(weComMassTaskMetrics);
    }

    public void sendMemberStatusDetail(String projectUuid, String corpId, WeComMassTaskTypeEnum taskType,
                                       String uuid, String taskUuid, String memberId, String planTime,
                                       String sentTime, WeComMassTaskMemberStatusEnum status,
                                       Integer externalUserCount, Boolean finish) {
        WeComTaskCenterMetrics weComMassTaskMetrics = new WeComTaskCenterMetrics();
        weComMassTaskMetrics.setProjectUuid(projectUuid);
        weComMassTaskMetrics.setCorpId(corpId);
        weComMassTaskMetrics.setTaskType(taskType);
        weComMassTaskMetrics.setTaskUuid(taskUuid);
        weComMassTaskMetrics.setUuid(uuid);
        if (StringUtils.isNotBlank(planTime)) {
            weComMassTaskMetrics.setPlanTime(planTime);
        }
        weComMassTaskMetrics.setMetricType(MASS_TASK_MEMBER_DETAIL);
        WeComTaskCenterMetrics.MemberMessage memberMessage =
                new WeComTaskCenterMetrics.MemberMessage();

        List<WeComTaskCenterMetrics.MemberStatus> statuses = new ArrayList<>();

        WeComTaskCenterMetrics.MemberStatus memberStatus =
                new WeComTaskCenterMetrics.MemberStatus();
        memberStatus.setMemberId(memberId);
        memberStatus.setStatus(status);
        if (StringUtils.isNotBlank(sentTime)) {
            memberStatus.setTime(sentTime);
        }
        memberStatus.setExternalUserCount(externalUserCount);
        statuses.add(memberStatus);
        memberMessage.setMemberState(statuses);
        memberMessage.setFinish(finish);
        weComMassTaskMetrics.setMemberMessage(memberMessage);
        produceRabbitMqMessage(weComMassTaskMetrics);
    }

    protected void sendTaskCenterNotify(String projectUuid, String corpId, String agentId,
                                        WeComMassTaskTypeEnum taskType,
                                        String uuid, String taskUuid, String memberId, String planTime,
                                        String taskName, Integer targetTime, String targetType) {
        weComAgentMessageService.sendTaskCenterMessage(projectUuid, taskType.getName(), corpId, agentId, taskUuid,
                uuid, taskName, Arrays.asList(memberId), planTime, targetTime, targetType);
    }
}
