package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.WeComDeleteGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 2:55 PM
 * Describe:
 */

public interface WeComWelcomeMsgRpcService {
    RpcResponse sendWelcomeMsg(WeComSendWelcomeMsgClientRequest request);

    RpcResponse groupChatWelcomeMsg(WeComGroupChatWelcomeMsgClientRequest request);

    RpcResponse deleteGroupChatWelcomeMsg(WeComDeleteGroupChatWelcomeMsgClientRequest request);
}
