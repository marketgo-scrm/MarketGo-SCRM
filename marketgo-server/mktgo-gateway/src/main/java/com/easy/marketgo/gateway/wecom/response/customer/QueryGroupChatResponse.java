package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 11:49 PM
 * Describe:
 */
@Data
public class QueryGroupChatResponse extends WeComBaseResponse {

    @JsonProperty("group_chat_list")
    private List<GroupChatListMessage> groupChatList;

    @JsonProperty("next_cursor")
    private String nextCursor;

    @Data
    public static class GroupChatListMessage {
        @JsonProperty("chat_id")
        private String chatId;
        private Integer status;
    }
}
