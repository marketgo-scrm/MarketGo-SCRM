package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.codec.Base64;
import com.easy.marketgo.api.model.request.masstask.WeComMassTaskClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComSendMassTaskClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private RedisService redisService;

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
        log.info("query send queue for single task center . entity={}", entity);
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
            WeComMassTaskMemberStatusEnum status = WeComMassTaskMemberStatusEnum.UNSENT;
            for (WeComRelationMemberExternalUserEntity userEntity : entities) {
                //memberId##uuid##taskUuid##externaluserid##base64(name)
                String key = String.format("%s##%s##%s##%s##%s", memberId, sendData.getUuid(), taskUuid,
                        userEntity.getExternalUserId(), Base64.encode(userEntity.getExternalUserName()));
                log.info("save external user message for single task center to cache . key={}", key);
                redisService.set(key, WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), 0L);
            }
            redisService.set(String.format("%s##%s##%s", memberId, sendData.getUuid(), taskUuid),
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue(), 0L);
            sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId, sendData.getUuid(), externalUserList,
                    WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
            sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                    WeComMassTaskTypeEnum.SINGLE, sendData.getUuid(), taskUuid, memberId,
                    status, totalCount, Boolean.TRUE);

        }
    }
}
