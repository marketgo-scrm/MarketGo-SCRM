package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendIdType;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSyncStatisticEntity;
import com.easy.marketgo.core.model.bo.WeComMassTaskMetricsBO;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSyncStatisticRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_EXTERNAL_USER_DETAIL;
import static com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/5/22 10:44 AM
 * Describe:
 */
@Slf4j
@Component
public class SendMassTaskBaseConsumer {

    @Autowired
    private RabbitTemplate weComMassTaskStatisticAmqpTemplate;

    @Autowired
    private WeComMassTaskSyncStatisticRepository weComMassTaskSyncStatisticRepository;

    protected void produceRabbitMqMessage(Object values) {
        weComMassTaskStatisticAmqpTemplate.convertAndSend(RabbitMqConstants.EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_STATISTIC, RabbitMqConstants.ROUTING_KEY_WECOM_MASS_TASK_STATISTIC, values);
    }

    protected void saveMassTaskStatistic(List<WeComMassTaskSyncStatisticEntity> entities) {
        weComMassTaskSyncStatisticRepository.saveAll(entities);
    }

    protected void saveMassTaskStatistic(WeComMassTaskSyncStatisticEntity entity) {
        weComMassTaskSyncStatisticRepository.save(entity);
    }

    protected void sendExternalUserStatusDetail(String projectUuid, String corpId, WeComMassTaskTypeEnum taskType,String taskUuid, String memberId, List<String> externalUsers,
                                                WeComMassTaskExternalUserStatusEnum status, Boolean finish) {
        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setMetricType(MASS_TASK_EXTERNAL_USER_DETAIL);
        weComMassTaskMetricsBO.setProjectUuid(projectUuid);
        weComMassTaskMetricsBO.setCorpId(corpId);
        weComMassTaskMetricsBO.setTaskType(taskType);
        WeComMassTaskMetricsBO.ExternalUserMessage externalUserMessage =
                new WeComMassTaskMetricsBO.ExternalUserMessage();

        List<WeComMassTaskMetricsBO.ExternalUserStatus> statuses = new ArrayList<>();

        externalUsers.forEach(externalUser -> {
            WeComMassTaskMetricsBO.ExternalUserStatus externalUserStatus =
                    new WeComMassTaskMetricsBO.ExternalUserStatus();
            externalUserStatus.setExternalUserId(externalUser);
            externalUserStatus.setStatus(status);
            statuses.add(externalUserStatus);
        });
        externalUserMessage.setExternalUserStatus(statuses);
        externalUserMessage.setMemberId(memberId);
        externalUserMessage.setFinish(finish);
        weComMassTaskMetricsBO.setExternalUserMessage(externalUserMessage);
        produceRabbitMqMessage(weComMassTaskMetricsBO);
    }

    protected void sendMemberStatusDetail(String projectUuid, String corpId, WeComMassTaskTypeEnum taskType,
                                          String taskUuid, String memberId, String sendIds,
                                          WeComMassTaskMemberStatusEnum status,
                                          Integer externalUserCount, Boolean finish) {
        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setProjectUuid(projectUuid);
        weComMassTaskMetricsBO.setCorpId(corpId);
        weComMassTaskMetricsBO.setTaskType(taskType);
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setMetricType(MASS_TASK_MEMBER_DETAIL);
        WeComMassTaskMetricsBO.MemberMessage memberMessage =
                new WeComMassTaskMetricsBO.MemberMessage();

        List<WeComMassTaskMetricsBO.MemberStatus> statuses = new ArrayList<>();

        WeComMassTaskMetricsBO.MemberStatus memberStatus =
                new WeComMassTaskMetricsBO.MemberStatus();
        memberStatus.setMemberId(memberId);
        memberStatus.setStatus(status);
        memberStatus.setExternalUserCount(externalUserCount);
        statuses.add(memberStatus);
        memberMessage.setMemberState(statuses);
        memberMessage.setFinish(finish);
        memberMessage.setMsgId(sendIds);
        weComMassTaskMetricsBO.setMemberMessage(memberMessage);
        produceRabbitMqMessage(weComMassTaskMetricsBO);
    }

    protected void saveMassTaskSendId(String taskUuid, String sendId,
                                      WeComMassTaskTypeEnum taskType, WeComMassTaskSendIdType type) {
        WeComMassTaskSyncStatisticEntity weComMassTaskSyncStatisticEntity =
                new WeComMassTaskSyncStatisticEntity();
        weComMassTaskSyncStatisticEntity.setUuid(UuidUtils.generateUuid());
        weComMassTaskSyncStatisticEntity.setSendIdType(type);
        weComMassTaskSyncStatisticEntity.setDeleted(Boolean.FALSE);
        weComMassTaskSyncStatisticEntity.setSendId(sendId);
        weComMassTaskSyncStatisticEntity.setTaskUuid(taskUuid);
        weComMassTaskSyncStatisticEntity.setTaskType(taskType);
        saveMassTaskStatistic(weComMassTaskSyncStatisticEntity);
    }
}
