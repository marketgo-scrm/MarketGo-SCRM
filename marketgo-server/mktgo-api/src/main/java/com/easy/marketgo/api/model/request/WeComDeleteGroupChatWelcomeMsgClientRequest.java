package com.easy.marketgo.api.model.request;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:04 PM
 * Describe:
 */
@Data
public class WeComDeleteGroupChatWelcomeMsgClientRequest extends BaseRpcRequest {
    private String templateId;
}
