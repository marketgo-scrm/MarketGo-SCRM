package com.easy.marketgo.react.service.callback;

import com.easy.marketgo.core.model.callback.WeComForwardCallbackMsg;
import com.easy.marketgo.core.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.easy.marketgo.common.constants.RabbitMqConstants.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/22/22 7:22 PM
 * Describe:
 */

@Slf4j
@Service
public class CallbackForwardService {

    @Autowired
    private RabbitTemplate weComForwardMemberCallbackMsgAmqpTemplate;
    @Autowired
    private RabbitTemplate weComForwardExternalUserCallbackMsgAmqpTemplate;

    @Autowired
    private RedisService redisService;

    @Async
    public String acceptContactsCallbackAuthUrl(String msgSignature, String timestamp, String nonce, String echostr,
                                                String corpId) {
        String sEchoStr = "";

        log.info("forward callback message for contact get request. msgSignature={}, corpId={}, nonce={}, echostr={}",
                msgSignature, corpId, nonce, echostr);
        try {

            WeComForwardCallbackMsg weComForwardCallbackMsg = new WeComForwardCallbackMsg();
            weComForwardCallbackMsg.setCorpId(corpId);
            weComForwardCallbackMsg.setEchostr(echostr);
            weComForwardCallbackMsg.setMsgSignature(msgSignature);
            weComForwardCallbackMsg.setTimestamp(timestamp);
            weComForwardCallbackMsg.setNonce(nonce);
            weComForwardCallbackMsg.setMsgType(HttpMethod.GET);
            sendMemberMassageToMq(weComForwardCallbackMsg);
        } catch (Exception e) {
            log.error(
                    "failed to forward callback message for contact get request. msgSignature={}, timestamp={}, nonce={}, sCorpID={}, " +
                            "echostr={}", msgSignature, timestamp, nonce, corpId, echostr, e);
        }
        log.info("finish to forward callback message for contact get request. echostr={}, sEchoStr={}", echostr, sEchoStr);
        return sEchoStr;
    }

    @Async
    @Transactional
    public void acceptContactsCallbackEvent(String msgSignature, String timestamp, String nonce, String msgEvent,
                                            String corpId) {
        log.info("forward callback message for contact post request. msgSignature={}, corpId={}, nonce={}, msgEvent={}",
                msgSignature, corpId, nonce, msgEvent);

        try {
            WeComForwardCallbackMsg weComForwardCallbackMsg = new WeComForwardCallbackMsg();
            weComForwardCallbackMsg.setCorpId(corpId);
            weComForwardCallbackMsg.setMsgSignature(msgSignature);
            weComForwardCallbackMsg.setTimestamp(timestamp);
            weComForwardCallbackMsg.setNonce(nonce);
            weComForwardCallbackMsg.setMsgEvent(msgEvent);
            weComForwardCallbackMsg.setMsgType(HttpMethod.POST);
            sendMemberMassageToMq(weComForwardCallbackMsg);
        } catch (Exception e) {
            log.error(
                    "failed to forward callback message for contact post request. msgSignature={}, timestamp={}, nonce={}, " +
                            "corpId={}, msgEvent={}", msgSignature, timestamp, nonce, corpId, msgEvent, e);
        }
    }

    @Async
    public String acceptCustomerCallbackAuthUrl(String msgSignature, String timestamp, String nonce, String echostr,
                                                String corpId) {

        String sEchoStr = "";

        log.info("forward callback message for customer get request. msgSignature={}, corpId={}, nonce={}, echostr={}",
                msgSignature, corpId, nonce, echostr);
        try {
            WeComForwardCallbackMsg weComForwardCallbackMsg = new WeComForwardCallbackMsg();
            weComForwardCallbackMsg.setCorpId(corpId);
            weComForwardCallbackMsg.setMsgSignature(msgSignature);
            weComForwardCallbackMsg.setTimestamp(timestamp);
            weComForwardCallbackMsg.setNonce(nonce);
            weComForwardCallbackMsg.setEchostr(sEchoStr);
            weComForwardCallbackMsg.setMsgType(HttpMethod.GET);
            sendExternalUserMassageToMq(weComForwardCallbackMsg);

        } catch (Exception e) {
            log.error(
                    "failed to forward callback message for customer get request. msgSignature={}, timestamp={}, nonce={}, " +
                            "sCorpID={}, echostr={}", msgSignature, timestamp, nonce, corpId, echostr, e);
        }
        log.info("finish to forward callback message for customer get request  from weCom. echostr={}, sEchoStr={}", echostr, sEchoStr);
        return sEchoStr;
    }

    @Async
    @Transactional
    public void acceptCustomerCallbackEvent(String msgSignature, String timestamp, String nonce, String msgEvent,
                                            String corpId) {

        log.info("forward callback message for customer post request. msgSignature={}, corpId={}, nonce={}, " +
                        "msgEvent={}",
                msgSignature, corpId, nonce, msgEvent);

        try {
            WeComForwardCallbackMsg weComForwardCallbackMsg = new WeComForwardCallbackMsg();
            weComForwardCallbackMsg.setCorpId(corpId);
            weComForwardCallbackMsg.setMsgSignature(msgSignature);
            weComForwardCallbackMsg.setTimestamp(timestamp);
            weComForwardCallbackMsg.setNonce(nonce);
            weComForwardCallbackMsg.setMsgEvent(msgEvent);
            weComForwardCallbackMsg.setMsgType(HttpMethod.POST);
            sendExternalUserMassageToMq(weComForwardCallbackMsg);
        } catch (Exception e) {
            log.error(
                    "failed to forward callback message for customer post request. msgSignature={}, timestamp={}, nonce={}, " +
                            "corpId={}, msgEvent={}", msgSignature, timestamp, nonce, corpId,
                    msgEvent, e);
        }
        log.info("finish to forward callback message for customer post request  from weCom. nonce={}, timestamp={}",
                nonce, timestamp);
    }

    private void sendMemberMassageToMq(Object values) {
        weComForwardMemberCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_WECOM_MEMBER_FORWARD,
                ROUTING_KEY_WECOM_MEMBER_FORWARD, values);
    }

    private void sendExternalUserMassageToMq(Object values) {
        weComForwardExternalUserCallbackMsgAmqpTemplate.convertAndSend(EXCHANGE_NAME_WECOM_EXTERNAL_USER_FORWARD,
                ROUTING_KEY_WECOM_EXTERNAL_USER_FORWARD, values);
    }
}
