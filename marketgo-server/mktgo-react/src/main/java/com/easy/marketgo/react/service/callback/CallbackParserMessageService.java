package com.easy.marketgo.react.service.callback;

import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import com.easy.marketgo.core.model.callback.WeComCorpTagMsg;
import com.easy.marketgo.core.model.callback.WeComExternalUserMsg;
import com.easy.marketgo.core.model.callback.WeComGroupChatMsg;
import com.easy.marketgo.core.model.callback.WeComMemberMsg;
import com.easy.marketgo.core.repository.wecom.WeComCorpMessageRepository;
import com.easy.marketgo.react.service.callback.auth.AesException;
import com.easy.marketgo.react.service.callback.auth.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Arrays;

import static com.easy.marketgo.common.constants.RabbitMqConstants.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/22/22 7:22 PM
 * Describe:
 */

@Slf4j
@Service
public class CallbackParserMessageService {

    @Autowired
    private WeComCorpMessageRepository weComCorpMessageRepository;

    @Autowired
    private RabbitTemplate weComMemberCallbackMsgAmqpTemplate;
    @Autowired
    private RabbitTemplate weComGroupChatCallbackMsgAmqpTemplate;

    @Autowired
    private RabbitTemplate weComCorpTagsCallbackMsgAmqpTemplate;

    @Autowired
    private RabbitTemplate weComExternalUserCallbackMsgAmqpTemplate;

    public String acceptContactsCallbackAuthUrl(String msgSignature, String timestamp, String nonce, String echostr,
                                                String corpId) {
        String sEchoStr = "";
        WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);

        log.info("get corp message from db. corpId={}, weComCorpMessageEntity={}", corpId, weComCorpMessageEntity);
        try {
            WXBizMsgCrypt wxcpt =
                    new WXBizMsgCrypt(weComCorpMessageEntity.getContactsToken(),
                            weComCorpMessageEntity.getContactsEncodingAesKey(), corpId);
            sEchoStr = wxcpt.verifyURL(msgSignature, timestamp, nonce, echostr);

        } catch (AesException e) {
            log.error(
                    "failed to decode message for auth url. msgSignature={}, timestamp={}, nonce={}, sCorpID={}, " +
                            "echostr={}", msgSignature, timestamp, nonce, corpId, echostr, e);
        }
        log.info("finish to auth member message url from weCom. echostr={}, sEchoStr={}", echostr, sEchoStr);
        return sEchoStr;
    }

    @Async
    @Transactional
    public void acceptContactsCallbackEvent(String msgSignature, String timestamp, String nonce, String msgEvent,
                                            String corpId) {
        WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
        String callbackCorpId = getToUserName(msgEvent);
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(weComCorpMessageEntity.getContactsToken(),
                    weComCorpMessageEntity.getContactsEncodingAesKey(), callbackCorpId);
            String decryptMsg = wxcpt.decryptMsg(msgSignature, timestamp, nonce, msgEvent);
            log.info("decrypt contacts message from weCom. decryptMsg={}", decryptMsg);
            WeComXmlMessage callBack = WeComXmlMessage.fromXml(decryptMsg);
            log.info("decrypt contacts message to WeComXmlMessage. WeComXmlMessage={}", callBack);
            WeComMemberMsg weComMemberMsg = new WeComMemberMsg();
            weComMemberMsg.setChangeType(callBack.getChangeType());
            weComMemberMsg.setCorpId(callbackCorpId);
            weComMemberMsg.setMemberId(callBack.getUserID());
            if (callBack.getDepartments() != null) {
                weComMemberMsg.setDepartments(Arrays.asList(callBack.getDepartments()));
            }
            if (StringUtils.isNotEmpty(callBack.getNewUserID())) {
                weComMemberMsg.setNewMemberId(callBack.getNewUserID());
            }
            log.info("send contacts message to mq. weComMemberMsg={}", weComMemberMsg);
            sendMemberMassageToMq(weComMemberMsg);
        } catch (Exception e) {
            log.error(
                    "failed to decode member message. msgSignature={}, timestamp={}, nonce={}, " +
                            "sCorpID={}, msgEvent={}",
                    msgSignature, timestamp, nonce, callbackCorpId, msgEvent, e);
        }
    }


    public String acceptCustomerCallbackAuthUrl(String msgSignature, String timestamp, String nonce, String echostr,
                                                String corpId) {

        String sEchoStr = "";
        WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);

        log.info("get corp message from db. corpId={}, weComCorpMessageEntity={}", corpId, weComCorpMessageEntity);
        try {
            WXBizMsgCrypt wxcpt =
                    new WXBizMsgCrypt(weComCorpMessageEntity.getExternalUserToken(),
                            weComCorpMessageEntity.getExternalUserEncodingAesKey(), corpId);
            sEchoStr = wxcpt.verifyURL(msgSignature, timestamp, nonce, echostr);

        } catch (AesException e) {
            log.error(
                    "failed to decode external user message for auth url. msgSignature={}, timestamp={}, nonce={}, " +
                            "sCorpID={}, " +
                            "echostr={}", msgSignature, timestamp, nonce, corpId, echostr, e);
        }
        log.info("finish to decode external user message  from weCom. echostr={}, sEchoStr={}", echostr, sEchoStr);
        return sEchoStr;
    }

    @Async
    @Transactional
    public void acceptCustomerCallbackEvent(String msgSignature, String timestamp, String nonce, String msgEvent,
                                            String corpId) {

        WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
        String callbackCorpId = getToUserName(msgEvent);

        try {
            WXBizMsgCrypt weComcpt = new WXBizMsgCrypt(weComCorpMessageEntity.getExternalUserToken(),
                    weComCorpMessageEntity.getExternalUserEncodingAesKey(), callbackCorpId);
            String decryptMsg = weComcpt.decryptMsg(msgSignature, timestamp, nonce, msgEvent);
            log.info("decrypt customer message from weCom. decryptMsg={}", decryptMsg);

            WeComXmlMessage callBack = WeComXmlMessage.fromXml(decryptMsg);

            log.info("finish to decrypt external user message from weCom. callBack={}", callBack);

            if (callBack.getEvent().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_EVENT_CORP_TAG)) {
                acceptCorpTagCallbackEvent(callBack, corpId);
            } else if (callBack.getEvent().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_EVENT_GROUP_CHAT)) {
                acceptGroupChatCallbackEvent(callBack, corpId);
            } else {
                acceptCustomerCallbackEvent(callBack, corpId);
            }

        } catch (Exception e) {
            log.error(
                    "failed to decode message for customer event. msgSignature={}, timestamp={}, nonce={}, " +
                            "callbackCorpId={}, msgEvent={}", msgSignature, timestamp, nonce, callbackCorpId,
                    msgEvent, e);
        }
    }

    private void acceptGroupChatCallbackEvent(WeComXmlMessage message, String corpId) {

        WeComGroupChatMsg weComGroupChatMsg = new WeComGroupChatMsg();
        weComGroupChatMsg.setChangeType(message.getChangeType());
        weComGroupChatMsg.setCorpId(corpId);
        weComGroupChatMsg.setChatId(message.getChatId());
        if (StringUtils.isNotEmpty(message.getUpdateDetail())) {
            weComGroupChatMsg.setUpdateDetail(message.getUpdateDetail());
        }
        if (message.getJoinScene() != null) {
            weComGroupChatMsg.setJoinScene(message.getJoinScene());
        }

        if (message.getQuitScene() != null) {
            weComGroupChatMsg.setQuitScene(message.getQuitScene());
        }

        if (message.getMemChangeCnt() != null) {
            weComGroupChatMsg.setMemberChangeCount(message.getMemChangeCnt());
        }
        weComGroupChatMsg.setCreateTime(message.getCreateTime());
        log.info("send group chat callback msg. weComGroupChatMsg={}", weComGroupChatMsg);
        sendGroupChatMassageToMq(weComGroupChatMsg);
    }

    private void acceptCustomerCallbackEvent(WeComXmlMessage message, String corpId) {
        WeComExternalUserMsg weComExternalUserMsg = new WeComExternalUserMsg();
        weComExternalUserMsg.setCorpId(corpId);
        weComExternalUserMsg.setChangeType(message.getChangeType());
        weComExternalUserMsg.setMemberId(message.getUserID());
        weComExternalUserMsg.setExternalUserId(message.getExternalUserId());
        if (StringUtils.isNotEmpty(message.getState())) {
            weComExternalUserMsg.setState(message.getState());
        }
        if (StringUtils.isNotEmpty(message.getWelcomeCode())) {
            weComExternalUserMsg.setWelcomeCode(message.getWelcomeCode());
        }
        if (StringUtils.isNotEmpty(message.getSource())) {
            weComExternalUserMsg.setSource(message.getSource());
        }
        if (StringUtils.isNotEmpty(message.getFailReason())) {
            weComExternalUserMsg.setFailReason(message.getFailReason());
        }
        weComExternalUserMsg.setCreateTime(message.getCreateTime());
        log.info("send external user callback msg. weComExternalUserMsg={}", weComExternalUserMsg);
        sendExternalUserMassageToMq(weComExternalUserMsg);
        sendExternalUserMassageLiveCodeToMq(weComExternalUserMsg);
    }

    private void acceptCorpTagCallbackEvent(WeComXmlMessage message, String corpId) {
        WeComCorpTagMsg weComCorpTagMsg = new WeComCorpTagMsg();
        weComCorpTagMsg.setCorpId(corpId);
        weComCorpTagMsg.setChangeType(message.getChangeType());
        weComCorpTagMsg.setTagId(message.getId());
        weComCorpTagMsg.setTagType(message.getTagType());
        if (message.getStrategyId() != null) {
            weComCorpTagMsg.setStrategyId(message.getStrategyId());
        }
        log.info("send corp tag callback msg. weComCorpTagMsg={}", weComCorpTagMsg);
        sendCorpTaqMassageToMq(weComCorpTagMsg);
    }

    private String getToUserName(String message) {
        String content = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(message);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("ToUserName");
            content = nodelist1.item(0).getTextContent();
        } catch (Exception e) {
            log.error(
                    "failed to parse xml to get toUserName. message={}", message, e);
        }
        return content;
    }

    private void sendMemberMassageToMq(Object values) {
        weComMemberCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_MEMBER,
                ROUTING_KEY_WECOM_MEMBER, values);
    }

    private void sendGroupChatMassageToMq(Object values) {
        weComGroupChatCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_GROUP_CHAT,
                ROUTING_KEY_WECOM_GROUP_CHAT, values);
    }

    private void sendCorpTaqMassageToMq(Object values) {
        weComCorpTagsCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_CORP_TAG,
                ROUTING_KEY_WECOM_CORP_TAG, values);
    }

    private void sendExternalUserMassageToMq(Object values) {
        weComExternalUserCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_EXTERNAL_USER,
                ROUTING_KEY_WECOM_EXTERNAL_USER, values);
    }

    private void sendExternalUserMassageLiveCodeToMq(Object values) {
        weComExternalUserCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_LIVE_CODE_EXTERNAL_USER ,
                ROUTING_KEY_WECOM_LIVE_CODE_EXTERNAL_USER, values);
    }
}
