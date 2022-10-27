package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.WeComSendAgentMessageClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/6/22 9:58 PM
 * Describe:
 */
public interface WeComSendAgentMessageRpcService {
    RpcResponse sendAgentMessage(WeComSendAgentMessageClientRequest request);
}
