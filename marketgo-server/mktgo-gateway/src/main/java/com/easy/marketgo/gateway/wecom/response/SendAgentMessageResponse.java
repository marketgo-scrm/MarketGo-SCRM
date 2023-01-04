package com.easy.marketgo.gateway.wecom.response;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/6/22 10:37 PM
 * Describe:
 */
@Data
public class SendAgentMessageResponse extends WeComBaseResponse {
    @JsonProperty("invaliduser")
    private String invalidUser;
    @JsonProperty("invalidparty")
    private String invalidParty;
    @JsonProperty("invalidtag")
    private String invalidTag;
    @JsonProperty("msgid")
    private String msgId;
    @JsonProperty("response_code")
    private String responseCode;
}
