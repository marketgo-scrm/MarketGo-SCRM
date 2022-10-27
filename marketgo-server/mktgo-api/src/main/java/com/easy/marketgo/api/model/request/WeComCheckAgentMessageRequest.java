package com.easy.marketgo.api.model.request;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 20:49
 * Describe:
 */
@Data
public class WeComCheckAgentMessageRequest extends BaseRpcRequest {
    private String corpId;
    private String agentId;
    private String secret;
}
