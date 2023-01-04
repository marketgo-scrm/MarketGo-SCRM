package com.easy.marketgo.core.model.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 12:09
 * Describe:
 */
@Data
public class QueryAgentAccessTokenResponse extends WeComBaseResponse {
    @JsonProperty(value = "access_token")
    private String accessToken;
    @JsonProperty(value = "expires_in")
    private Integer expiresIn;
}
