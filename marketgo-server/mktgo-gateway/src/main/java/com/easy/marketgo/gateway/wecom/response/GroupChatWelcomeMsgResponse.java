package com.easy.marketgo.gateway.wecom.response;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:25 PM
 * Describe:
 */
@Data
public class GroupChatWelcomeMsgResponse extends WeComBaseResponse {
    @JsonProperty("template_id")
    private String templateId;
}
