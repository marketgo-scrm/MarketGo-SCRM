package com.easy.marketgo.gateway.wecom.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:04 PM
 * Describe:
 */
@Data
public class DeleteContactWayRequest {
    @JsonProperty("config_id")
    private String configId;
}
