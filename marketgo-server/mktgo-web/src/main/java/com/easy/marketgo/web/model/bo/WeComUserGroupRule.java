package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 7:48 PM
 * Describe:
 */
@Data
@ApiModel(description = "WECOM人群计算的条件详情")
public class WeComUserGroupRule {
    @ApiModelProperty(value = "员工的人群条件详情")
    private MembersMessage members;
    @ApiModelProperty(value = "客户的人群条件详情")
    private ExternalUserMessage externalUsers;
    @ApiModelProperty(value = "排除客户的开关")
    private boolean excludeSwitch;
    @ApiModelProperty(value = "排除客户的人群条件详情")
    private ExternalUserMessage excludeExternalUsers;
    @ApiModelProperty(value = "客户群的条件详情")
    private GroupChatsMessage groupChats;
    @ApiModelProperty(value = "排除客户群的开关")
    private boolean excludeGroupChatSwitch;
    @ApiModelProperty(value = "排除客户群条件详情")
    private GroupChatsMessage excludeGroupChats;

    @Data
    public static class MembersMessage {
        @ApiModelProperty(value = "是否是全部员工")
        private Boolean isAll;
        @ApiModelProperty(value = "部门的列表")
        private List<DepartmentMessage> departments;
        @ApiModelProperty(value = "员工列表")
        private List<UserMessage> users;
    }

    @Data
    public static class UserMessage {
        @ApiModelProperty(value = "员工id")
        private String memberId;
        @ApiModelProperty(value = "员工名称")
        private String memberName;
    }

    @Data
    public static class DepartmentMessage {
        @ApiModelProperty(value = "部门id")
        private Long id;
        @ApiModelProperty(value = "部门名称")
        private String name;
    }

    @Data
    public static class TagsMessage{
        @ApiModelProperty(value = "条件之间的关系， OR 或， AND 且")
        private String relation;
        @ApiModelProperty(value = "标签列表")
        private List<WeComCorpTag> tags;
    }

    @Data
    public static class GroupChatsMessage {
        @ApiModelProperty(value = "是否是全部客户群")
        private Boolean isAll;
        @ApiModelProperty(value = "条件之间的关系， OR 或， AND 且")
        private String relation;
        @ApiModelProperty(value = "客户群名称选中的开关")
        private boolean groupChatNameSwitch;
        @ApiModelProperty(value = "客户群创建时间选中的开关")
        private boolean createTimeSwitch;
        @ApiModelProperty(value = "客户群列表选中开关")
        private boolean groupChatsSwitch;
        @ApiModelProperty(value = "客户群人数选中开关")
        private boolean userCountSwitch;
        @ApiModelProperty(value = "客户群人数的规则， GT 大于，GTE 大于或等于，LT 小于，LTE 小于或等于，EQ 等于 NEQ 不等于")
        private String userCountRule;
        @ApiModelProperty(value = "客户群人数")
        private Integer userCount;
        @ApiModelProperty(value = "客户群名称")
        private String groupChatName;
        @ApiModelProperty(value = "客户群列表")
        private List<GroupChatMessage> groupChats;
        @ApiModelProperty(value = "创建的开始时间")
        private String startTime;
        @ApiModelProperty(value = "创建的结束时间")
        private String endTime;
    }


    @Data
    public static class ExternalUserMessage {
        @ApiModelProperty(value = "是否是全部客户")
        private Boolean isAll;
        @ApiModelProperty(value = "条件之间的关系， OR 或， AND 且")
        private String relation;
        @ApiModelProperty(value = "标签选中的开关")
        private boolean corpTagSwitch;
        @ApiModelProperty(value = "客户添加时间选中开发")
        private boolean addTimeSwitch;
        @ApiModelProperty(value = "客户群选中开关")
        private boolean groupChatsSwitch;
        @ApiModelProperty(value = "性别选中开关")
        private boolean genderSwitch;
        @ApiModelProperty(value = "性别列表")
        private List<Integer> genders;
        @ApiModelProperty(value = "标签条件")
        private TagsMessage corpTags;
        @ApiModelProperty(value = "客户群列表")
        private List<GroupChatMessage> groupChats;
        @ApiModelProperty(value = "添加的开始时间")
        private String startTime;
        @ApiModelProperty(value = "添加的结束时间")
        private String endTime;
    }
}



