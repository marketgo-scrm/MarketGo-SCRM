package com.easy.marketgo.biz.service.wecom.callback;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.biz.service.wecom.masstask.SendMassTaskBaseConsumer;
import com.easy.marketgo.biz.service.wecom.customer.SyncContactsService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComRelationType;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.callback.WeComExternalUserEventEntity;
import com.easy.marketgo.core.model.callback.WeComExternalUserMsg;
import com.easy.marketgo.core.repository.wecom.callback.WeComExternalUserEventRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 9:22 PM
 * Describe:
 */
@Slf4j
@Component
public class ExternalUserCallbackMsgConsumer extends SendMassTaskBaseConsumer {

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComExternalUserEventRepository weComExternalUserEventRepository;

    @Autowired
    private SyncContactsService syncContactsService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_EXTERNAL_USER}, containerFactory =
            "weComExternalUserCallbackMsgListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer external user callback message. message={}", data);
            WeComExternalUserMsg sendData = JsonUtils.toObject(data, WeComExternalUserMsg.class);
            saveWeComExternalUserMsg(sendData);
        } catch (Exception e) {
            log.error("failed to consumer external user callback message. message={}", data, e);
        }
    }

    private void saveWeComExternalUserMsg(WeComExternalUserMsg sendData) {
        WeComExternalUserEventEntity weComExternalUserEventEntity = new WeComExternalUserEventEntity();
        weComExternalUserEventEntity.setMemberId(sendData.getMemberId());
        weComExternalUserEventEntity.setCorpId(sendData.getCorpId());
        weComExternalUserEventEntity.setEventType(sendData.getChangeType());
        weComExternalUserEventEntity.setExternalUserId(sendData.getExternalUserId());
        if (StringUtils.isNotEmpty(sendData.getState())) {
            weComExternalUserEventEntity.setState(sendData.getState());
        }
        if (StringUtils.isNotEmpty(sendData.getWelcomeCode())) {
            weComExternalUserEventEntity.setWelcomeCode(sendData.getWelcomeCode());
        }
        if (StringUtils.isNotEmpty(sendData.getSource())) {
            weComExternalUserEventEntity.setSource(sendData.getSource());
        }
        if (StringUtils.isNotEmpty(sendData.getFailReason())) {
            weComExternalUserEventEntity.setFailReason(sendData.getFailReason());
        }
        weComExternalUserEventEntity.setEventTime(DateUtil.date(sendData.getCreateTime() * 1000));
        String toString = weComExternalUserEventEntity.toString();
        weComExternalUserEventEntity.setEventMd5(SecureUtil.md5(toString));
        log.info("save WeComExternalUserEventEntity. toString={}, weComExternalUserEventEntity={}",
                toString, weComExternalUserEventEntity);
        weComExternalUserEventRepository.save(weComExternalUserEventEntity);
        if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_ADD_CUSTOMER) ||
                sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_EDIT_CUSTOMER)) {
            syncContactsService.syncExternalUserDetail(sendData.getCorpId(), sendData.getExternalUserId());
        } else if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CUSTOMER)) {
            weComRelationMemberExternalUserRepository.deleteRelationByExternalUserAndMemberId(sendData.getCorpId(),
                    sendData.getMemberId(), sendData.getExternalUserId(), WeComRelationType.MEMBER_DEL.ordinal());
        } else if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_FOLLOW_USER)) {
            weComRelationMemberExternalUserRepository.deleteRelationByExternalUserAndMemberId(sendData.getCorpId(),
                    sendData.getMemberId(), sendData.getExternalUserId(),
                    WeComRelationType.EXTERNAL_USER_DEL.ordinal());
        }
    }
}
