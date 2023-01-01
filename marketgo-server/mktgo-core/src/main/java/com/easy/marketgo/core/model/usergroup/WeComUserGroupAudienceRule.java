package com.easy.marketgo.core.model.usergroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/25/22 10:04 PM
 * Describe:
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeComUserGroupAudienceRule {
    private MembersMessage members = null;
    private ExternalUserMessage externalUsers = null;
    private boolean excludeSwitch = false;
    private ExternalUserMessage excludeExternalUsers = null;

    private GroupChatsMessage groupChats = null;
    private boolean excludeGroupChatSwitch = false;
    private GroupChatsMessage excludeGroupChats = null;

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
    public static class TagsMessage {
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

    @Data
    public static class GroupChatsMessage {
        //是否是全部客户群
        private Boolean isAll;
        //条件之间的关系， OR 或， AND 且
        private String relation;
        //客户群名称选中的开关
        private boolean groupChatNameSwitch;
        //客户群创建时间选中的开关
        private boolean createTimeSwitch;
        // 客户群列表选中开关
        private boolean groupChatsSwitch;
        //客户群人数选中开关
        private boolean userCountSwitch;
        // 客户群人数的规则， GT 大于，GTE 大于或等于，LT 小于，LTE 小于或等于，EQ 等于 NEQ 不等于
        private String userCountRule;
        //客户群人数
        private Integer userCount;
        //客户群名称
        private String groupChatName;
        //客户群列表
        private List<GroupChatMessage> groupChats;
        // 创建的开始时间
        private String startTime;
        // 创建的结束时间
        private String endTime;
    }
}
