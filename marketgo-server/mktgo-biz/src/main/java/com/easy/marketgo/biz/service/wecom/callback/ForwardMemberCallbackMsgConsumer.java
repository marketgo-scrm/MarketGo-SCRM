package com.easy.marketgo.biz.service.wecom.callback;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.callback.WeComForwardCallbackMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/8/22 9:22 PM
 * Describe:
 */
@Slf4j
@Component
public class ForwardMemberCallbackMsgConsumer extends ForwardCallbackMsgBaseConsumer {

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_MEMBER_FORWARD}, containerFactory =
            "weComForwardMemberCallbackMsgListenerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer forward member callback message. message={}", data);
            WeComForwardCallbackMsg sendData = JsonUtils.toObject(data, WeComForwardCallbackMsg.class);
            sendWeComMemberCallbackMsg(sendData);
        } catch (Exception e) {
            log.error("failed to consumer member callback message. message={}", data, e);
        }
    }
}
