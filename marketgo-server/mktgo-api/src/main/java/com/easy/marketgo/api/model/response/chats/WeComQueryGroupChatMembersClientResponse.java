package com.easy.marketgo.api.model.response.chats;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 10:37 AM
 * Describe:
 */
@Data
public class WeComQueryGroupChatMembersClientResponse implements Serializable {
    private GroupChatMessage groupChat;

    @Data
    public static class GroupChatMessage implements Serializable {
        private String chatId;

        private String name;

        private String owner;
        private Long createTime;

        private String notice;

        private List<MemberListMessage> memberList;

        private List<InvitorMessage> adminList;
    }

    @Data
    public static class MemberListMessage implements Serializable {
        private String userId;

        private String name;

        private Integer type;
        private Long joinTime;
        private Integer joinScene;
        private String unionId;
        private String groupNickname;
        private InvitorMessage invitor;
    }

    @Data
    public static class InvitorMessage implements Serializable {
        private String userId;
    }
}
