package com.easy.marketgo.core.model.wecom;

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
    protected String corpId;
    protected String agentId;
    protected String secret;
}
