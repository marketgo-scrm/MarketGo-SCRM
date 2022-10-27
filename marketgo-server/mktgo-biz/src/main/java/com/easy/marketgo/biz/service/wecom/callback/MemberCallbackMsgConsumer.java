package com.easy.marketgo.biz.service.wecom.callback;

import com.easy.marketgo.api.model.request.customer.QueryMemberDetailClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.customer.QueryMemberDetailClientResponse;
import com.easy.marketgo.api.service.WeComMemberRpcService;
import com.easy.marketgo.biz.service.wecom.masstask.SendMassTaskBaseConsumer;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComRelationType;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.callback.WeComMemberMsg;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 9:22 PM
 * Describe:
 */
@Slf4j
@Component
public class MemberCallbackMsgConsumer extends SendMassTaskBaseConsumer {

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Resource
    private WeComMemberRpcService weComMemberRpcService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MEMBER}, containerFactory =
            "weComMemberCallbackMsgListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer member callback message. message={}", data);
            WeComMemberMsg sendData = JsonUtils.toObject(data, WeComMemberMsg.class);
            saveWeComMemberMsg(sendData);
        } catch (Exception e) {
            log.error("failed to consumer member callback message. message={}", data, e);
        }
    }

    private void saveWeComMemberMsg(WeComMemberMsg sendData) {
        WeComMemberMessageEntity weComMemberMessageEntity = new WeComMemberMessageEntity();

        WeComAgentMessageEntity agentMessageEntity =
                weComAgentMessageRepository.getAgentMessageByCorpId(sendData.getCorpId());
        log.info("query agent message for mass task. agentMessageEntity={}", agentMessageEntity);
        String agentId = (agentMessageEntity == null ? "" : agentMessageEntity.getAgentId());

        if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_CREATE_USER)) {

            QueryMemberDetailClientResponse response = queryMemberDetail(sendData.getCorpId(), agentId,
                    sendData.getMemberId());
            if (response == null) {
                log.error("failed to query member detail. corpId={}, agentId={}, memberId={}",
                        sendData.getCorpId(), agentId, sendData.getMemberId());
                return;
            }
            weComMemberMessageEntity.setMemberName(response.getName());
            weComMemberMessageEntity.setMemberId(sendData.getMemberId());
            weComMemberMessageEntity.setCorpId(sendData.getCorpId());
            weComMemberMessageEntity.setStatus(response.getStatus());
            if (CollectionUtils.isNotEmpty(response.getDepartment())) {
                weComMemberMessageEntity.setDepartment(response.getDepartment().stream().map(String::valueOf).collect(Collectors.joining(","
                )));
            }
            weComMemberMessageRepository.save(weComMemberMessageEntity);
        } else if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_UPDATE_USER)) {
            String memberId = StringUtils.isNoneEmpty(sendData.getNewMemberId()) ? sendData.getNewMemberId() :
                    sendData.getMemberId();
            QueryMemberDetailClientResponse response = queryMemberDetail(sendData.getCorpId(), agentId, memberId);
            if (response == null) {
                log.error("failed to query member detail. corpId={}, agentId={}, memberId={}",
                        sendData.getCorpId(), agentId, sendData.getMemberId());
                return;
            }

            weComMemberMessageEntity =
                    weComMemberMessageRepository.getMemberMessgeByMemberId(sendData.getCorpId(),
                            sendData.getMemberId());
            if (weComMemberMessageEntity != null) {
                if (StringUtils.isNotEmpty(sendData.getNewMemberId())) {
                    weComMemberMessageEntity.setMemberId(sendData.getNewMemberId());

                }
                weComMemberMessageEntity.setStatus(response.getStatus());
                if (CollectionUtils.isNotEmpty(response.getDepartment())) {
                    weComMemberMessageEntity.setDepartment(response.getDepartment().stream().map(String::valueOf).collect(Collectors.joining(","
                    )));
                }
                weComMemberMessageEntity.setMemberName(response.getName());
                weComMemberMessageRepository.save(weComMemberMessageEntity);

                if (StringUtils.isNotEmpty(sendData.getNewMemberId())) {
                    weComRelationMemberExternalUserRepository.updateMemberByMemberId(sendData.getCorpId(),
                            sendData.getMemberId(), sendData.getNewMemberId());
                }
            }
        } else if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_USER)) {
            weComMemberMessageRepository.deleteByCorpIdAAndMemberId(sendData.getCorpId(), sendData.getMemberId());
            weComRelationMemberExternalUserRepository.updateRelationByMemberId(sendData.getCorpId(),
                    sendData.getMemberId(), WeComRelationType.MEMBER_DEL.ordinal());
        }
    }

    private QueryMemberDetailClientResponse queryMemberDetail(String corpId, String agentId, String memberId) {
        QueryMemberDetailClientRequest queryMemberDetailClientRequest = new QueryMemberDetailClientRequest();
        queryMemberDetailClientRequest.setCorpId(corpId);
        queryMemberDetailClientRequest.setUserId(memberId);
        queryMemberDetailClientRequest.setAgentId(agentId);
        RpcResponse<QueryMemberDetailClientResponse> rpcResponse =
                weComMemberRpcService.queryMemberDetail(queryMemberDetailClientRequest);
        log.info("query member detail rpc response. rpcResponse={}", rpcResponse);
        return rpcResponse.getData();
    }
}
