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
public class WeComAuth2VerifyResponse extends WeComBaseResponse {
    @JsonProperty("UserId")
    private String userId = null;

    @JsonProperty("DeviceId")
    private String deviceId = null;
}
