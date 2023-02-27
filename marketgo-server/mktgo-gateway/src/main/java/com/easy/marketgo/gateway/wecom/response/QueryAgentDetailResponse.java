package com.easy.marketgo.gateway.wecom.response;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 20:01
 * Describe:
 */
@Data
public class QueryAgentDetailResponse extends WeComBaseResponse {
    private Integer agentid = null;

    private String name = null;
}
