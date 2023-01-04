package com.easy.marketgo.react.model;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 10:19 AM
 * Describe:
 */
@Data
public class WeComApiTicketResponse extends WeComBaseResponse {
    private String ticket = null;
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
