package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComWelcomeMsgRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.SendWelcomeMsgRequest;
import com.easy.marketgo.gateway.wecom.sevice.SendWelcomeMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:35 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComWelcomeMsgRpcServiceImpl implements WeComWelcomeMsgRpcService {
    @Autowired
    private SendWelcomeMsgService sendWelcomeMsgService;

    @Override
    public RpcResponse sendWelcomeMsg(WeComSendWelcomeMsgClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        SendWelcomeMsgRequest sendWelcomeMsgRequest = new SendWelcomeMsgRequest();
        BeanUtils.copyProperties(request, sendWelcomeMsgRequest);
        List<SendWelcomeMsgRequest.AttachmentsMessage> attachments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getAttachments())) {
            request.getAttachments().forEach(attach -> {
                SendWelcomeMsgRequest.AttachmentsMessage message = new SendWelcomeMsgRequest.AttachmentsMessage();
                if (attach.getImage() != null) {
                    SendWelcomeMsgRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                            new SendWelcomeMsgRequest.ImageAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getImage(), imageAttachmentsMessage);
                    message.setImage(imageAttachmentsMessage);
                } else if (attach.getLink() != null) {
                    SendWelcomeMsgRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                            new SendWelcomeMsgRequest.LinkAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getLink(), linkAttachmentsMessage);
                    message.setLink(linkAttachmentsMessage);
                } else if (attach.getMiniprogram() != null) {
                    SendWelcomeMsgRequest.MiniProgramAttachmentsMessage miniProgramAttachmentsMessage =
                            new SendWelcomeMsgRequest.MiniProgramAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getMiniprogram(), miniProgramAttachmentsMessage);
                    message.setMiniprogram(miniProgramAttachmentsMessage);
                } else if (attach.getVideo() != null) {
                    SendWelcomeMsgRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                            new SendWelcomeMsgRequest.VideoAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getVideo(), videoAttachmentsMessage);
                    message.setVideo(videoAttachmentsMessage);
                } else if (attach.getFile() != null) {
                    SendWelcomeMsgRequest.FileAttachmentsMessage fileAttachmentsMessage =
                            new SendWelcomeMsgRequest.FileAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getFile(), fileAttachmentsMessage);
                    message.setFile(fileAttachmentsMessage);
                }
                BeanUtils.copyProperties(attach, message);
                attachments.add(message);
            });
        }
        SendWelcomeMsgRequest.TextMessage textMessage = new SendWelcomeMsgRequest.TextMessage();
        BeanUtils.copyProperties(request.getText(), textMessage);
        sendWelcomeMsgRequest.setText(textMessage);
        sendWelcomeMsgRequest.setAttachments(attachments);
        return sendWelcomeMsgService.sendWelcomeMsg(request.getCorpId(), request.getAgentId(), sendWelcomeMsgRequest);
    }
}
