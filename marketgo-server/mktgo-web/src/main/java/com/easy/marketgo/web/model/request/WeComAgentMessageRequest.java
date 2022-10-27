package com.easy.marketgo.web.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/15/22 1:41 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微配置内建应用信息request")
public class WeComAgentMessageRequest {
    private String corpId = null;

    private String agentId = null;

    private String secret = null;
}
