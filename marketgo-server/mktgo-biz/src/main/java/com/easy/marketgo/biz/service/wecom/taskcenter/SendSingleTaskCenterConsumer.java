package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.taskcenter.SendTaskCenterBaseConsumer;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/12/22 9:22 PM
 * Describe:
 */
@Slf4j
@Component
public class SendSingleTaskCenterConsumer extends SendTaskCenterBaseConsumer {

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_TASK_CENTER_SINGLE}, containerFactory =
            "weComSingleTaskCenterListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer single task center message. message={}", data);
            WeComTaskCenterRequest sendData = JsonUtils.toObject(data, WeComTaskCenterRequest.class);
            sendSingleTaskCenter(sendData);
        } catch (Exception e) {
            log.error("failed to consumer single task center message. message={}", data, e);
        }
    }

    private void sendSingleTaskCenter(WeComTaskCenterRequest sendData) {
        String memberId = sendData.getSender();
        String taskUuid = sendData.getTaskUuid();
        WeComMassTaskSendQueueEntity entity = weComMassTaskSendQueueRepository.queryByTaskUuidAndMember(taskUuid,
                memberId, WeComMassTaskSendStatusEnum.UNSEND.name());
        log.info("query send queue for single task center. entity={}", entity);
        if (entity != null) {
            weComMassTaskSendQueueRepository.updateStatusByTaskUuidAndMember(WeComMassTaskSendStatusEnum.UNSEND.name(),
                    WeComMassTaskSendStatusEnum.SEND.name(), taskUuid, memberId);
            String externalUserIds = entity.getExternalUserIds();
            List<String> externalUserList = Arrays.asList(externalUserIds.split(","));
            log.info("send external user count for single task center . externalUserCount={}", externalUserList.size());

            List<WeComRelationMemberExternalUserEntity> entities =
                    weComRelationMemberExternalUserRepository.queryExternalUserByCorpId(sendData.getCorpId(),
                            externalUserList);
            Integer totalCount = externalUserList.size();
            if (CollectionUtils.isEmpty(entities)) {
                log.error("query externalUser message is empty for task center.");
                return;
            }
            for (WeComRelationMemberExternalUserEntity userEntity : entities) {
                taskCacheManagerService.setCustomerCache(sendData.getCorpId(), memberId, taskUuid, sendData.getUuid(),
                        userEntity.getExternalUserId(), userEntity.getExternalUserName(), userEntity.getAvatar());
            }
            taskCacheManagerService.setMemberCache(sendData.getCorpId(), memberId, taskUuid, sendData.getUuid(),
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue());
            sendTaskCenterNotify(sendData.getProjectUuid(), sendData.getCorpId(), sendData.getAgentId(),
                    WeComMassTaskTypeEnum.SINGLE,
                    sendData.getUuid(), sendData.getTaskUuid(), sendData.getSender(), sendData.getPlanTime(),
                    sendData.getTaskName(), sendData.getTargetTime(), sendData.getTargetType());
            sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId, sendData.getUuid(), externalUserList,
                    sendData.getPlanTime(), "", WeComMassTaskExternalUserStatusEnum.UNDELIVERED,
                    Boolean.TRUE);
            sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(), WeComMassTaskTypeEnum.SINGLE,
                    sendData.getUuid(), taskUuid, memberId, sendData.getPlanTime(),"",
                    WeComMassTaskMemberStatusEnum.UNSENT, totalCount, Boolean.TRUE);

            weComMassTaskSendQueueRepository.deleteSendQueueByUuid(entity.getUuid());
        }
    }
}
