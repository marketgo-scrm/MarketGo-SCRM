package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.WeComMomentMassTaskClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComSendMomentMassTaskClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @data : 7/8/22 9:26 PM
 * Describe:
 */
@Slf4j
@Component
public class SendMomentMassTaskConsumer extends SendMassTaskBaseConsumer {

    @Resource
    private WeComMassTaskRpcService weComMassTaskRpcService;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MASS_TASK_MOMENT}, containerFactory =
            "weComMomentMassTaskListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer moment mass task message. message={}", data);
        WeComMomentMassTaskClientRequest sendData = JsonUtils.toObject(data, WeComMomentMassTaskClientRequest.class);
        sendMomentMassTask(sendData);
    }

    private void sendMomentMassTask(WeComMomentMassTaskClientRequest sendData) {

        String taskUuid = sendData.getTaskUuid();
        List<WeComMassTaskSendQueueEntity> entities = weComMassTaskSendQueueRepository.queryByTaskUuid(taskUuid,
                WeComMassTaskSendStatusEnum.UNSEND.name());
        if (CollectionUtils.isEmpty(entities)) {
            log.info("consumer moment mass task message, query send queue is empty. taskUuid={}", taskUuid);
            return;
        }
        entities.forEach(entity -> {
            try {
                String tagIds = entity.getExternalUserIds();
                List<String> tagIdList = new ArrayList<>();
                if (StringUtils.isNotBlank(tagIds)) {
                    tagIdList.addAll(Arrays.asList(tagIds.split(",")));
                    WeComMomentMassTaskClientRequest.ExternalContactListMessage externalContactListMessage =
                            new WeComMomentMassTaskClientRequest.ExternalContactListMessage();
                    externalContactListMessage.setTagList(tagIdList);
                    sendData.getVisibleRange().setExternalContactList(externalContactListMessage);
                }
                weComMassTaskSendQueueRepository.updateStatusByTaskUuid(WeComMassTaskSendStatusEnum.UNSEND.name(),
                        WeComMassTaskSendStatusEnum.SEND.name(), taskUuid);
                RpcResponse<WeComSendMomentMassTaskClientResponse> rpcResponse =
                        weComMassTaskRpcService.sendMomentMassTask(sendData);
                log.info("send moment mass task response. response={}", rpcResponse);
                WeComSendMomentMassTaskClientResponse weComSendMomentMassTaskClientResponse = rpcResponse.getData();
                if (weComSendMomentMassTaskClientResponse != null && rpcResponse.getSuccess()) {
                    for (String memberId : sendData.getVisibleRange().getSenderList().getUserList()) {
                        Integer count = queryExternalUserList(sendData.getProjectUuid(), sendData.getCorpId(),
                                sendData.getTaskUuid(), memberId, tagIdList);
                        sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                                WeComMassTaskTypeEnum.MOMENT, taskUuid, memberId,
                                weComSendMomentMassTaskClientResponse.getJobId(),
                                WeComMassTaskMemberStatusEnum.UNSENT, count, Boolean.TRUE);
                    }
                    saveMassTaskSendId(taskUuid, weComSendMomentMassTaskClientResponse.getJobId(),
                            WeComMassTaskTypeEnum.MOMENT, WeComMassTaskSendIdType.JOB_ID);
                }
            } catch (Exception e) {
                log.error("failed to send moment mass task. entity={}", entity, e);
            }
        });
    }

    private Integer queryExternalUserList(String projectUuid, String corpId, String taskUuid, String memberId,
                                          List<String> tags) {
        QueryUserGroupBuildSqlParam param =
                QueryUserGroupBuildSqlParam.builder().corpId(corpId).memberIds(Arrays.asList(memberId)).tagRelation(
                        "OR").tags(tags).build();
        List<WeComRelationMemberExternalUserEntity> entities =
                weComRelationMemberExternalUserRepository.listByUserGroupCnd(param);

        log.info("query external user for moment mass task. memberId={}, entities={}", memberId, entities);
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(entities)) {
            List<String> externalUserList = new ArrayList<>();
            entities.forEach(entity -> {
                externalUserList.add(entity.getExternalUserId());
            });
            List<String> externalUsers = externalUserList.stream().distinct().collect(Collectors.toList());
            count = externalUsers.size();
            sendExternalUserStatusDetail(projectUuid, corpId, WeComMassTaskTypeEnum.MOMENT, taskUuid, memberId,
                    externalUsers, WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
        }
        return count;
    }
}
