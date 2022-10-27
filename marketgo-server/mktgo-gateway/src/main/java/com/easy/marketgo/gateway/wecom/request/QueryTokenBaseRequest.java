package com.easy.marketgo.gateway.wecom.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 11:06
 * Describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryTokenBaseRequest {
    private String corpId;
    private String agentId;
    private String secret;
}
