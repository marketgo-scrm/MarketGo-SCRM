package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.codec.Base64;
import com.easy.marketgo.api.model.request.masstask.WeComMassTaskClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComSendMassTaskClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class SendGroupTaskCenterCenterConsumer extends SendTaskCenterBaseConsumer {

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private RedisService redisService;

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
                //memberId##uuid##taskUuid##externaluserid##base64(name)
                String key = String.format("%s##%s##%s##%s##%s", memberId, sendData.getUuid(), taskUuid,
                        userEntity.getCorpId(), Base64.encode(userEntity.getGroupChatName()));
                log.info("save group chat message for group task center to cache . key={}", key);
                redisService.set(key, WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), 0L);
            }
            redisService.set(String.format("%s##%s##%s", memberId, sendData.getUuid(), taskUuid),
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), 0L);
            sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.GROUP, taskUuid, memberId, sendData.getUuid(), externalUserList,
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
            sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.GROUP, sendData.getUuid(), taskUuid, memberId,
                    status, totalCount, Boolean.TRUE);

        }
    }
}
