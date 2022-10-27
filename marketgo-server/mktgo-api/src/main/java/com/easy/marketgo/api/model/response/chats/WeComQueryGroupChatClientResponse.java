package com.easy.marketgo.api.model.response.chats;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 11:49 PM
 * Describe:
 */
@Data
public class WeComQueryGroupChatClientResponse implements Serializable {

    private List<GroupChatListMessage> groupChatList;

    private String nextCursor;

    @Data
    public static class GroupChatListMessage implements Serializable {
        private String chatId;
        private Integer status;
    }
}
