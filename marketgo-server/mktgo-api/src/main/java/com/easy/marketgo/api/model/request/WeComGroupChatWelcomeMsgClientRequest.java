package com.easy.marketgo.api.model.request;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:04 PM
 * Describe:
 */
@Data
public class WeComGroupChatWelcomeMsgClientRequest extends BaseRpcRequest {
    private String templateId;
    private Integer notify;

    private WeComSendWelcomeMsgClientRequest.TextMessage text;
    private WeComSendWelcomeMsgClientRequest.ImageAttachmentsMessage image;
    private WeComSendWelcomeMsgClientRequest.LinkAttachmentsMessage link;
    private WeComSendWelcomeMsgClientRequest.MiniProgramAttachmentsMessage miniprogram;
}
