package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/18/22 5:29 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户群成员response")
public class WeComGroupChatMembersResponse {
    @ApiModelProperty(value = "企业的客户群成员总数")
    private Integer totalCount;
    @ApiModelProperty(value = "企业的客户群列表")
    private List<GroupChatMember> groupChatMembers;

    @Data
    public static class GroupChatMember {
        @ApiModelProperty(value = "企业的客户群成员名称")
        private String name;
        @ApiModelProperty(value = "企业的客户群成员ID")
        private String userId;
        @ApiModelProperty(value = "企业的客户群成员头像")
        private String avatar;
        @ApiModelProperty(value = "成员类型 1-企业成员, 2-外部联系人")
        private Integer type;
        @ApiModelProperty(value = "成员在群里的昵称")
        private String nickName;
        @ApiModelProperty(value = "成员入群时间")
        private String joinTime;
        @ApiModelProperty(value = "入群方式, 1 - 由群成员邀请入群（直接邀请入群）2 - 由群成员邀请入群（通过邀请链接入群）,3 - 通过扫描群二维码入群")
        private Integer joinScene;
    }
}
