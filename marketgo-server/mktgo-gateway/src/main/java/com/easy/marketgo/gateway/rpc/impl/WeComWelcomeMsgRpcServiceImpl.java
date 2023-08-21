package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.WeComDeleteGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComWelcomeMsgRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.welcome.GroupChatAddWelcomeMsgRequest;
import com.easy.marketgo.gateway.wecom.request.welcome.GroupChatDeleteWelcomeMsgRequest;
import com.easy.marketgo.gateway.wecom.request.welcome.GroupChatEditWelcomeMsgRequest;
import com.easy.marketgo.gateway.wecom.request.welcome.SendWelcomeMsgRequest;
import com.easy.marketgo.gateway.wecom.sevice.SendWelcomeMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
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

    @Override
    public RpcResponse groupChatWelcomeMsg(WeComGroupChatWelcomeMsgClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("add group chat welcome msg. request={}", request);
        if (StringUtils.isEmpty(request.getTemplateId())) {
            GroupChatAddWelcomeMsgRequest addRequest = new GroupChatAddWelcomeMsgRequest();

            addRequest.setNotify(request.getNotify());
            addRequest.setAgentid(Integer.valueOf(request.getAgentId()));
            if (request.getText() != null) {
                GroupChatAddWelcomeMsgRequest.TextMessage text = new GroupChatAddWelcomeMsgRequest.TextMessage();
                BeanUtils.copyProperties(request.getText(), text);
                addRequest.setText(text);
            }
            if (request.getImage() != null) {
                GroupChatAddWelcomeMsgRequest.ImageAttachmentsMessage image =
                        new GroupChatAddWelcomeMsgRequest.ImageAttachmentsMessage();
                BeanUtils.copyProperties(request.getImage(), image);
                addRequest.setImage(image);
            }
            if (request.getLink() != null) {
                GroupChatAddWelcomeMsgRequest.LinkAttachmentsMessage link =
                        new GroupChatAddWelcomeMsgRequest.LinkAttachmentsMessage();
                BeanUtils.copyProperties(request.getLink(), link);
                addRequest.setLink(link);
            }
            if (request.getMiniprogram() != null) {
                GroupChatAddWelcomeMsgRequest.MiniProgramAttachmentsMessage mini =
                        new GroupChatAddWelcomeMsgRequest.MiniProgramAttachmentsMessage();
                BeanUtils.copyProperties(request.getMiniprogram(), mini);
                addRequest.setMiniprogram(mini);
            }
            return sendWelcomeMsgService.groupChatWelcomeMsg(request.getCorpId(), request.getAgentId(), addRequest);
        } else {
            GroupChatEditWelcomeMsgRequest editRequest = new GroupChatEditWelcomeMsgRequest();
            if (request.getText() != null) {
                GroupChatAddWelcomeMsgRequest.TextMessage text = new GroupChatAddWelcomeMsgRequest.TextMessage();
                BeanUtils.copyProperties(request.getText(), text);
                editRequest.setText(text);
            }
            if (request.getImage() != null) {
                GroupChatAddWelcomeMsgRequest.ImageAttachmentsMessage image =
                        new GroupChatAddWelcomeMsgRequest.ImageAttachmentsMessage();
                BeanUtils.copyProperties(request.getImage(), image);
                editRequest.setImage(image);
            }
            if (request.getLink() != null) {
                GroupChatAddWelcomeMsgRequest.LinkAttachmentsMessage link =
                        new GroupChatAddWelcomeMsgRequest.LinkAttachmentsMessage();
                BeanUtils.copyProperties(request.getLink(), link);
                editRequest.setLink(link);
            }
            if (request.getMiniprogram() != null) {
                GroupChatAddWelcomeMsgRequest.MiniProgramAttachmentsMessage mini =
                        new GroupChatAddWelcomeMsgRequest.MiniProgramAttachmentsMessage();
                BeanUtils.copyProperties(request.getMiniprogram(), mini);
                editRequest.setMiniprogram(mini);
            }
            editRequest.setTemplateId(request.getTemplateId());
            return sendWelcomeMsgService.groupChatEditWelcomeMsg(request.getCorpId(), request.getAgentId(),
                    editRequest);
        }
    }

    @Override
    public RpcResponse deleteGroupChatWelcomeMsg(WeComDeleteGroupChatWelcomeMsgClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        GroupChatDeleteWelcomeMsgRequest deleteRequest = new GroupChatDeleteWelcomeMsgRequest();
        BeanUtils.copyProperties(request, deleteRequest);
        deleteRequest.setAgentid(Integer.valueOf(request.getAgentId()));
        return sendWelcomeMsgService.groupChatDeleteWelcomeMsg(request.getCorpId(), request.getAgentId(),
                deleteRequest);
    }
}
