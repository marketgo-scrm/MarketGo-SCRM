package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.WeComCheckAgentMessageRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 18:16
 * Describe:
 */
public interface WeComAgentRpcService {
    RpcResponse checkAgentParams(WeComCheckAgentMessageRequest request);
}
