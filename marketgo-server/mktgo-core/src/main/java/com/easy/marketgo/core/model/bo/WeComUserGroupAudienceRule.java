package com.easy.marketgo.core.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/25/22 10:04 PM
 * Describe:
 */
@Data
public class WeComUserGroupAudienceRule {
    private MembersMessage members;
    private ExternalUserMessage externalUsers;
    private boolean excludeSwitch;
    private ExternalUserMessage excludeExternalUsers;

    @Data
    public static class MembersMessage {
        private Boolean isAll;
        private List<DepartmentMessage> departments;
        private List<UserMessage> users;
    }

    @Data
    public static class UserMessage {
        private String memberId;
        private String memberName;
    }

    @Data
    public static class DepartmentMessage {
        private Long id;
        private String name;
    }

    @Data
    public static class TagsMessage{
        private String relation;
        private List<WeComCorpTag> tags;
    }

    @Data
    public static class ExternalUserMessage {
        private Boolean isAll;
        private String relation;
        private boolean corpTagSwitch;
        private boolean addTimeSwitch;
        private boolean groupChatsSwitch;
        private boolean genderSwitch;

        private List<Integer> genders;
        private TagsMessage corpTags;
        private List<GroupChatMessage> groupChats;
        private String startTime;
        private String endTime;
    }

    @Data
    public static class WeComCorpTag {
        private String id;
        private String name;
    }

    @Data
    public static class GroupChatMessage {
        private String chatId;
        private String chatName;
    }
}
