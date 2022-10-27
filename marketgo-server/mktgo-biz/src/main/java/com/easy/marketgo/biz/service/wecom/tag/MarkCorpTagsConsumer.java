package com.easy.marketgo.biz.service.wecom.tag;

import com.easy.marketgo.api.model.request.tag.WeComEditExternalUserCorpTagClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComExternalUserRpcService;
import com.easy.marketgo.biz.service.wecom.customer.SyncContactsService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.bo.WeComMarkCorpTagsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/19/22 3:19 PM
 * Describe:
 */

@Slf4j
@Component
public class MarkCorpTagsConsumer {

    @Resource
    private WeComExternalUserRpcService weComExternalUserRpcService;
    @Autowired
    private SyncContactsService syncContactsService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_EDIT_CORP_TAGS}, containerFactory =
            "weComEditCorpTagsListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());

        try {
            log.info("consumer mark corp tags message. message={}", data);
            WeComMarkCorpTagsMessage sendData = JsonUtils.toObject(data, WeComMarkCorpTagsMessage.class);
            WeComEditExternalUserCorpTagClientRequest request = new WeComEditExternalUserCorpTagClientRequest();
            request.setCorpId(sendData.getCorpId());
            request.setAgentId(sendData.getAgentId());
            request.setAddTag(sendData.getTagIds());
            request.setExternalUserId(sendData.getExternalUserId());
            request.setUserId(sendData.getMemberId());
            RpcResponse response = weComExternalUserRpcService.editExternalUserCorpTag(request);
            log.info(" mark corp tags response from weCom. response={}", response);
            if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                syncContactsService.syncExternalUserDetail(sendData.getCorpId(), sendData.getExternalUserId());
            }
        } catch (Exception e) {
            log.error("failed to mark corp tags message. message={}", data, e);
        }
    }
}
