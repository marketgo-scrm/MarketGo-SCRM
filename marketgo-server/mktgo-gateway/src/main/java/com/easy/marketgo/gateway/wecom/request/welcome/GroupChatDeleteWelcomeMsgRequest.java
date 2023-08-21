package com.easy.marketgo.gateway.wecom.request.welcome;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:50
 * Describe:
 */
@Data
public class GroupChatDeleteWelcomeMsgRequest {
    @JsonProperty("template_id")
    private String templateId;
    private Integer agentid;
}
