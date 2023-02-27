package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.taskcenter.SendTaskCenterBaseConsumer;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 9:25 PM
 * Describe:
 */
@Slf4j
@Component
public class SendGroupTaskCenterConsumer extends SendTaskCenterBaseConsumer {

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_TASK_CENTER_GROUP}, containerFactory =
            "weComGroupTaskCenterListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer group task  center message. message={}", data);
        WeComTaskCenterRequest sendData = JsonUtils.toObject(data, WeComTaskCenterRequest.class);
        sendGroupMassTask(sendData);
    }

    private void sendGroupMassTask(WeComTaskCenterRequest sendData) {
        String memberId = sendData.getSender();
        String taskUuid = sendData.getTaskUuid();
        WeComMassTaskSendQueueEntity entity = weComMassTaskSendQueueRepository.queryByTaskUuidAndMember(taskUuid,
                memberId, WeComMassTaskSendStatusEnum.UNSEND.name());
        if (entity != null) {
            weComMassTaskSendQueueRepository.updateStatusByTaskUuidAndMember(WeComMassTaskSendStatusEnum.UNSEND.name(),
                    WeComMassTaskSendStatusEnum.SEND.name(), taskUuid, memberId);

            String externalUserIds = entity.getExternalUserIds();
            List<String> externalUserList = Arrays.asList(externalUserIds.split(","));
            log.info("send group count for group task center . externalUserCount={}", externalUserList.size());

            List<WeComGroupChatsEntity> entities =
                    weComGroupChatsRepository.queryByChatIds(sendData.getCorpId(),
                            externalUserList);
            Integer totalCount = externalUserList.size();
            if (CollectionUtils.isEmpty(entities)) {
                log.error("query group chat message is empty for task center.");
                return;
            }
            WeComMassTaskMemberStatusEnum status = WeComMassTaskMemberStatusEnum.UNSENT;
            for (WeComGroupChatsEntity userEntity : entities) {
                taskCacheManagerService.setCustomerCache(sendData.getCorpId(), memberId, taskUuid, sendData.getUuid(),
                        userEntity.getCorpId(), userEntity.getGroupChatName(), "");
            }
            taskCacheManagerService.setMemberCache(sendData.getCorpId(), memberId, taskUuid, sendData.getUuid(),
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue());
            sendTaskCenterNotify(sendData.getProjectUuid(), sendData.getCorpId(), sendData.getAgentId(),
                    WeComMassTaskTypeEnum.GROUP,
                    sendData.getUuid(), sendData.getTaskUuid(), sendData.getSender(), sendData.getPlanTime(),
                    sendData.getTaskName(), sendData.getTargetTime(), sendData.getTargetType());
            sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.GROUP, taskUuid, memberId, sendData.getUuid(), externalUserList,
                    sendData.getPlanTime(), "",
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
            sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.GROUP, sendData.getUuid(), taskUuid, memberId, sendData.getPlanTime(), "",
                    status, totalCount, Boolean.TRUE);
            weComMassTaskSendQueueRepository.deleteSendQueueByUuid(entity.getUuid());
        }
    }
}
