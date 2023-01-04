package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 10:37 AM
 * Describe:
 */
@Data
public class QueryGroupChatMembersResponse extends WeComBaseResponse {
    @JsonProperty("group_chat")
    private GroupChatMessage groupChat;

    @Data
    public static class GroupChatMessage {
        @JsonProperty("chat_id")
        private String chatId;

        private String name;

        private String owner;
        @JsonProperty("create_time")
        private Long createTime;

        private String notice;

        @JsonProperty("member_list")
        private List<MemberListMessage> memberList;

        @JsonProperty("admin_list")
        private List<InvitorMessage> adminList;
    }

    @Data
    public static class MemberListMessage {
        @JsonProperty("userid")
        private String userId;

        private String name;

        private Integer type;
        @JsonProperty("join_time")
        private Long joinTime;
        @JsonProperty("join_scene")
        private Integer joinScene;
        @JsonProperty("unionid")
        private String unionId;

        @JsonProperty("group_nickname")
        private String groupNickname;
        private InvitorMessage invitor;

    }

    @Data
    public static class InvitorMessage {
        @JsonProperty("userid")
        private String userId;
    }
}
