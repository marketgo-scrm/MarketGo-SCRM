package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.WeComSendAgentMessageClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComSendAgentMessageRpcService;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.gateway.wecom.request.SendAgentMessageRequest;
import com.easy.marketgo.gateway.wecom.sevice.SendAgentMessageService;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/6/22 10:03 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComSendAgentMessageRpcServiceImpl implements WeComSendAgentMessageRpcService {

    public static final String TEXTCARD_DESCRIPTION = "<div>%s</div> <div>%s</div>";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    @Autowired
    private SendAgentMessageService sendAgentMessageService;

    @Override
    public RpcResponse sendAgentMessage(WeComSendAgentMessageClientRequest request) {

        return sendAgentMessageService.sendAgentMessage(request.getCorpId(), request.getAgentId(),
                constructWeComSendMessageReq(request));
    }

    private SendAgentMessageRequest constructWeComSendMessageReq(WeComSendAgentMessageClientRequest request) {
        SendAgentMessageRequest sendAgentMessageRequest = SendAgentMessageRequest.builder()
                .touser(Joiner.on("|").join(request.getToUser()))
                .msgtype(request.getMsgType().getValue().toLowerCase())
                .agentid(Integer.parseInt(request.getAgentId()))
                .enableDuplicateCheck(request.getEnableDuplicateCheck())
                .duplicateCheckInterval(request.getDuplicateCheckInterval())
                .build();
        String content = request.getContent();
        switch (request.getMsgType()) {
            case TEXTCARD:
                SendAgentMessageRequest.TextCard textcard =
                        JsonUtils.toObject(content, SendAgentMessageRequest.TextCard.class);
                String realContent = String.format(TEXTCARD_DESCRIPTION,
                        DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                        textcard.getDescription());
                textcard.setDescription(realContent);
                sendAgentMessageRequest.setTextcard(textcard);
                break;
            case TEXT:
                sendAgentMessageRequest.setText(JsonUtils.toObject(content, SendAgentMessageRequest.Text.class));
                break;
            case IMAGE:
                sendAgentMessageRequest.setImage(JsonUtils.toObject(content, SendAgentMessageRequest.Image.class));
                break;
            case NEWS:
                sendAgentMessageRequest.setNews(JsonUtils.toObject(content, SendAgentMessageRequest.News.class));
                break;
            case TEMPLATE_CARD:
                sendAgentMessageRequest.setTemplateCard(JsonUtils.toObject(content, SendAgentMessageRequest.TemplateCard.class));
                break;
        }
        return sendAgentMessageRequest;
    }
}
