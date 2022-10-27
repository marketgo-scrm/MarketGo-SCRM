package com.easy.marketgo.biz.service.wecom.tag;

import com.easy.marketgo.core.model.bo.WeComMarkCorpTags;
import com.easy.marketgo.core.model.bo.WeComMarkCorpTagsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.easy.marketgo.common.constants.RabbitMqConstants.EXCHANGE_NAME_DEFAULT_WECOM_EDIT_CORP_TAGS;
import static com.easy.marketgo.common.constants.RabbitMqConstants.ROUTING_KEY_WECOM_EDIT_CORP_TAGS;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/19/22 3:19 PM
 * Describe:
 */

@Slf4j
@Component
public class MarkCorpTagsProducer {

    @Autowired
    private RabbitTemplate weComEditCorpTagsAmqpTemplate;

    @Async
    public void markCorpTagForExternalUser(WeComMarkCorpTags tags) {

        if (tags == null) {
            log.info("tag message is empty");
            return;
        }
        if (CollectionUtils.isNotEmpty(tags.getExternalUsers())) {
            tags.getExternalUsers().forEach(item -> {
                WeComMarkCorpTagsMessage message = new WeComMarkCorpTagsMessage();
                message.setCorpId(tags.getCorpId());
                message.setAgentId(tags.getAgentId());
                message.setExternalUserId(item.getExternalUserId());
                message.setMemberId(item.getMemberId());
                if (CollectionUtils.isNotEmpty(tags.getTagIds())) {
                    message.setTagIds(tags.getTagIds());
                }
                if (CollectionUtils.isNotEmpty(tags.getDeleteTagIds())) {
                    message.setDeleteTagIds(tags.getDeleteTagIds());
                }
                log.info("mark corp tag  for external user. message={}", message);
                produceRabbitMqMessage(message);
            });
        }
    }

    private void produceRabbitMqMessage(Object values) {
        weComEditCorpTagsAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_EDIT_CORP_TAGS,
                ROUTING_KEY_WECOM_EDIT_CORP_TAGS, values);
    }
}
