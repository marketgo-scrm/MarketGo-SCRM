package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.WeComMassTaskClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.WeComSendMassTaskClientResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
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
 * @data : 7/8/22 9:22 PM
 * Describe:
 */
@Slf4j
@Component
public class SendSingleMassTaskConsumer extends SendMassTaskBaseConsumer {

    @Resource
    private WeComMassTaskRpcService weComMassTaskRpcService;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MASS_TASK_SINGLE}, containerFactory =
            "weComSingleMassTaskListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer single mass task message. message={}", data);
            WeComMassTaskClientRequest sendData = JsonUtils.toObject(data, WeComMassTaskClientRequest.class);
            sendSingleMassTask(sendData);
        } catch (Exception e) {
            log.error("failed to consumer single mass task message. message={}", data, e);
        }
    }

    private void sendSingleMassTask(WeComMassTaskClientRequest sendData) {
        String memberId = sendData.getSender();
        String taskUuid = sendData.getTaskUuid();
        WeComMassTaskSendQueueEntity entity = weComMassTaskSendQueueRepository.queryByTaskUuidAndMember(taskUuid,
                memberId, WeComMassTaskSendStatusEnum.UNSEND.name());
        log.info("query send queue for single mass task. entity={}", entity);
        if (entity != null) {
            weComMassTaskSendQueueRepository.updateStatusByTaskUuidAndMember(WeComMassTaskSendStatusEnum.UNSEND.name(),
                    WeComMassTaskSendStatusEnum.SEND.name(), taskUuid, memberId);
            String externalUserIds = entity.getExternalUserIds();
            List<String> externalUserList = Arrays.asList(externalUserIds.split(","));
            log.info("send external user count for single mass task. externalUserCount={}", externalUserList.size());
            List<List<String>> partitionList = Lists.partition(externalUserList,
                    Constants.WECOM_MASS_TASK_SINGLE_MAX_SIZE_EXTERNAL_USER);

            List<String> failList = new ArrayList<>();
            List<String> msgIdList = new ArrayList<>();
            Integer currentCount = 0;
            Integer totalCount = externalUserList.size();

            WeComMassTaskMemberStatusEnum status = WeComMassTaskMemberStatusEnum.UNSENT;
            for (List<String> partitionClientIdList : partitionList) {
                sendData.setExternalUserId(partitionClientIdList);
                RpcResponse<WeComSendMassTaskClientResponse> rpcResponse =
                        weComMassTaskRpcService.sendMassTask(sendData);

                log.info("send single mass task response. response={}", rpcResponse);
                WeComSendMassTaskClientResponse weComSendMassTaskClientResponse = rpcResponse.getData();
                if (weComSendMassTaskClientResponse != null && CollectionUtils.isNotEmpty(weComSendMassTaskClientResponse.getFailList())) {
                    log.info("response WeComSendMassTaskClientResponse failList is empty");
                    failList.addAll(weComSendMassTaskClientResponse.getFailList());
                }
                if (rpcResponse.getCode().equals(ErrorCodeEnum.ERROR_WECOM_USER_ID_IS_INVALID.getCode())) {
                    status = WeComMassTaskMemberStatusEnum.DIMISSION;
                } else if (!rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                    status = WeComMassTaskMemberStatusEnum.SENT_FAIL;
                }
                if (StringUtils.isNotEmpty(weComSendMassTaskClientResponse.getMsgId())) {
                    msgIdList.add(weComSendMassTaskClientResponse.getMsgId());

                    saveMassTaskSendId(taskUuid, weComSendMassTaskClientResponse.getMsgId(),
                            WeComMassTaskTypeEnum.SINGLE, WeComMassTaskSendIdType.MSG_ID);
                }
                currentCount += partitionClientIdList.size();
                sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                        WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId, partitionClientIdList,
                        WeComMassTaskExternalUserStatusEnum.UNDELIVERED,
                        (currentCount.equals(totalCount)) ? Boolean.TRUE : Boolean.FALSE);
            }
            if (CollectionUtils.isNotEmpty(msgIdList)) {
                sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                        WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId,
                        msgIdList.stream().collect(Collectors.joining(",")),
                        status, totalCount, Boolean.TRUE);
            } else {
                sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                        WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId, "",
                        status, totalCount, Boolean.TRUE);
            }

            if (CollectionUtils.isNotEmpty(failList)) {
                sendExternalUserStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                        WeComMassTaskTypeEnum.SINGLE, taskUuid, memberId, failList,
                        WeComMassTaskExternalUserStatusEnum.UNFRIEND,
                        Boolean.TRUE);
            }
        }
    }
}
