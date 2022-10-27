package com.easy.marketgo.biz.service.wecom.callback;

import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.biz.service.wecom.masstask.SendMassTaskBaseConsumer;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.callback.WeComCorpTagMsg;
import com.easy.marketgo.core.repository.wecom.customer.WeComCorpTagRepository;
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
public class CorpTagCallbackMsgConsumer extends SendMassTaskBaseConsumer {

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;

    @Autowired
    private WeComCorpTagRepository weComCorpTagRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_CORP_TAG}, containerFactory =
            "weComCorpTagsCallbackMsgListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        try {
            log.info("consumer corp tag callback message. message={}", data);
            WeComCorpTagMsg sendData = JsonUtils.toObject(data, WeComCorpTagMsg.class);
            saveWeComCorpTagMsg(sendData);
        } catch (Exception e) {
            log.error("failed to consumer corp tag message. message={}", data, e);
        }
    }

    private void saveWeComCorpTagMsg(WeComCorpTagMsg sendData) {
        if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CORP_TAG)) {
            weComCorpTagRepository.deleteByCorpIdAndTagId(sendData.getCorpId(), sendData.getTagId());
        } else {
            xxlJobManualTriggerService.manualTriggerHandler("syncCorpTags");
        }
    }
}
