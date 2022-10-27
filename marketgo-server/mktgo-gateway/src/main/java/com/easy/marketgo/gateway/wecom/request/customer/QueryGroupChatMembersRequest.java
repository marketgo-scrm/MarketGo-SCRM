package com.easy.marketgo.gateway.wecom.request.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 10:37 AM
 * Describe:
 */
@Data
public class QueryGroupChatMembersRequest {
    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("need_name")
    private Integer needName;
}
