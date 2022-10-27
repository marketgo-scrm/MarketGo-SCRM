package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.WeComMassTaskClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComSendMassTaskClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 9:25 PM
 * Describe:
 */
@Slf4j
@Component
public class SendGroupMassTaskConsumer extends SendMassTaskBaseConsumer {

    @Resource
    private WeComMassTaskRpcService weComMassTaskRpcService;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MASS_TASK_GROUP}, containerFactory =
            "weComGroupMassTaskListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer group mass task message. message={}", data);
        WeComMassTaskClientRequest sendData = JsonUtils.toObject(data, WeComMassTaskClientRequest.class);
        sendGroupMassTask(sendData);
    }

    private void sendGroupMassTask(WeComMassTaskClientRequest sendData) {
        String memberId = sendData.getSender();
        String taskUuid = sendData.getTaskUuid();
        WeComMassTaskSendQueueEntity entity = weComMassTaskSendQueueRepository.queryByTaskUuidAndMember(taskUuid,
                memberId, WeComMassTaskSendStatusEnum.UNSEND.name());
        if (entity != null) {
            weComMassTaskSendQueueRepository.updateStatusByTaskUuidAndMember(WeComMassTaskSendStatusEnum.UNSEND.name(),
                    WeComMassTaskSendStatusEnum.SEND.name(), taskUuid, memberId);
            RpcResponse<WeComSendMassTaskClientResponse> rpcResponse = weComMassTaskRpcService.sendMassTask(sendData);
            log.info("send group mass task response. response={}", rpcResponse);
            WeComSendMassTaskClientResponse weComSendMassTaskClientResponse = rpcResponse.getData();
            if (rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) && weComSendMassTaskClientResponse != null) {
                log.info("response WeComSendMassTaskClientResponse  is empty");
                Integer count = getGroupChatList(sendData.getProjectUuid(), sendData.getCorpId(), taskUuid, memberId);
                if (count > 0) {
                    sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(), WeComMassTaskTypeEnum.GROUP,
                            taskUuid, memberId, weComSendMassTaskClientResponse.getMsgId(),
                            WeComMassTaskMemberStatusEnum.UNSENT, count, Boolean.TRUE);

                    saveMassTaskSendId(taskUuid, weComSendMassTaskClientResponse.getMsgId(),
                            WeComMassTaskTypeEnum.GROUP,
                            WeComMassTaskSendIdType.MSG_ID);
                }
            } else {
                sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(), WeComMassTaskTypeEnum.GROUP,
                        taskUuid, memberId, "", WeComMassTaskMemberStatusEnum.DIMISSION, 0, Boolean.TRUE);
            }
        }
    }

    private Integer getGroupChatList(String projectUuid, String corpId, String taskUuid, String memberId) {
        List<WeComGroupChatsEntity> weComGroupChatsEntities =
                weComGroupChatsRepository.getGroupChatByOwner(corpId, memberId);

        log.info("query group chat for group mass task. memberId={}, weComGroupChatsEntities={}", memberId,
                weComGroupChatsEntities);
        if (CollectionUtils.isNotEmpty(weComGroupChatsEntities)) {
            List<String> groupChatList = new ArrayList<>();
            weComGroupChatsEntities.forEach(entity -> {
                groupChatList.add(entity.getGroupChatId());
            });
            sendExternalUserStatusDetail(projectUuid, corpId, WeComMassTaskTypeEnum.GROUP, taskUuid, memberId,
                    groupChatList, WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
        }
        return weComGroupChatsEntities.size();
    }
}
