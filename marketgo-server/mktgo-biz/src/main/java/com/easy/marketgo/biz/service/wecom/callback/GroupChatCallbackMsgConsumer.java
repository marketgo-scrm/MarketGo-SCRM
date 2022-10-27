package com.easy.marketgo.biz.service.wecom.callback;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.biz.service.wecom.masstask.SendMassTaskBaseConsumer;
import com.easy.marketgo.biz.service.wecom.customer.SyncGroupChatsService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.callback.WeComGroupChatEventEntity;
import com.easy.marketgo.core.model.callback.WeComGroupChatMsg;
import com.easy.marketgo.core.repository.wecom.callback.WeComGroupChatEventRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatMembersRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import lombok.extern.slf4j.Slf4j;
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
public class GroupChatCallbackMsgConsumer extends SendMassTaskBaseConsumer {

    @Autowired
    private SyncGroupChatsService syncGroupChatsService;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private WeComGroupChatMembersRepository weComGroupChatMembersRepository;

    @Autowired
    private WeComGroupChatEventRepository weComGroupChatEventRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_GROUP_CHAT}, containerFactory =
            "weComGroupChatCallbackMsgListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer group chats callback message. message={}", data);
            WeComGroupChatMsg sendData = JsonUtils.toObject(data, WeComGroupChatMsg.class);
            saveWeComGroupChatsMsg(sendData);
        } catch (Exception e) {
            log.error("failed to consumer group chats message. message={}", data, e);
        }
    }

    private void saveWeComGroupChatsMsg(WeComGroupChatMsg sendData) {
        WeComGroupChatEventEntity weComGroupChatEventEntity = new WeComGroupChatEventEntity();
        weComGroupChatEventEntity.setCorpId(sendData.getCorpId());
        weComGroupChatEventEntity.setEventType(sendData.getChangeType());
        weComGroupChatEventEntity.setGroupChatId(sendData.getChatId());
        weComGroupChatEventEntity.setUpdateDetail(sendData.getUpdateDetail());
        if (sendData.getJoinScene() != null) {
            weComGroupChatEventEntity.setJoinScene(sendData.getJoinScene());
        }

        if (sendData.getQuitScene() != null) {
            weComGroupChatEventEntity.setQuitScene(sendData.getQuitScene());
        }

        if (sendData.getMemberChangeCount() != null) {
            weComGroupChatEventEntity.setMemChangeCnt(sendData.getMemberChangeCount());
        }
        weComGroupChatEventEntity.setEventTime(DateUtil.date(sendData.getCreateTime() * 1000));
        String toString = weComGroupChatEventEntity.toString();
        weComGroupChatEventEntity.setEventMd5(SecureUtil.md5(toString));
        log.info("save WeComExternalUserEventEntity. toString={}, weComGroupChatEventEntity={}",
                toString, weComGroupChatEventEntity);

        weComGroupChatEventRepository.save(weComGroupChatEventEntity);
        if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DISMISS_GROUP_CHAT)) {
            weComGroupChatMembersRepository.deleteByCorpIdAndChatId(sendData.getCorpId(), sendData.getChatId());
            weComGroupChatsRepository.deleteByCorpIdAndChatId(sendData.getCorpId(), sendData.getChatId());
        } else {
            String chatId = sendData.getChatId();
            syncGroupChatsService.syncGroupChat(sendData.getCorpId(), chatId);
        }
    }
}
