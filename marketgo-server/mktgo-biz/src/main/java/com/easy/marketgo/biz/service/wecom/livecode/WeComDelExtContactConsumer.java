package com.easy.marketgo.biz.service.wecom.livecode;

import com.easy.marketgo.biz.service.wecom.livecode.ChannelLiveCodeRefreshService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-16 20:47:21
 * @description : WeComDelExtContactConsumer.java
 */
@Component
@Log4j2
public class WeComDelExtContactConsumer {
    @Autowired
    private ChannelLiveCodeRefreshService liveCodeRefreshService;
    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_DEL_EXT_CONTACT}, containerFactory =
            "weComChangeExtContactListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {

        String data = new String(message.getBody());
        try {
            log.info(" message={}", data);
            WeComXmlMessage weComXmlMessage = JsonUtils.toObject(data, WeComXmlMessage.class);
            doLiveStaticTask(weComXmlMessage);


        } catch (Exception e) {
            log.error("failed to consumer message. message={}", data, e);
        }
    }

    private void doLiveStaticTask(WeComXmlMessage weComXmlMessage) {
        liveCodeRefreshService.statisticRefresh(weComXmlMessage);

    }

}
