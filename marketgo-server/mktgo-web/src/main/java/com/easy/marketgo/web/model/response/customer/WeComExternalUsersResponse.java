package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/21/22 4:05 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户列表response")
public class WeComExternalUsersResponse {
    @ApiModelProperty(value = "企业的客户总数")
    private Integer totalCount;
    @ApiModelProperty(value = "企业的客户详情列表")
    private List<ExternalUser> externalUsers;

    @Data
    public static class ExternalUser {
        @ApiModelProperty(value = "企业员工名称")
        private String memberName;
        @ApiModelProperty(value = "企业员工ID")
        private String memberId;
        @ApiModelProperty(value = "客户ID")
        private String externalUserId;
        @ApiModelProperty(value = "客户名称")
        private String externalUserName;
        @ApiModelProperty(value = "客户类型。1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
        private Integer type;
        @ApiModelProperty(value = "头像url")
        private String avatar;
        @ApiModelProperty(value = "外部联系人性别, 0-未知 1-男性 2-女性")
        private Integer gender;
        @ApiModelProperty(value = "客户所在客户群列表")
        private List<GroupChat> groupChats;
        @ApiModelProperty(value = "客户备注")
        private String remark;
        @ApiModelProperty(value = "员工在客户设置的标签")
        private List<ExternalUserFollowUserTag> tags;
        @ApiModelProperty(value = "客户添加时间")
        private Date addTime;
        @ApiModelProperty(value = "客户添加来源")
        private Integer addWay;
    }

    @Data
    public static class ExternalUserFollowUserTag {
        @ApiModelProperty(value = "标签名称")
        private String tagName = null;
        @ApiModelProperty(value = "标签id")
        private String tagId = null;
    }

    @Data
    public static class GroupChat {
        @ApiModelProperty(value = "客户群名称")
        private String groupChatName;
        @ApiModelProperty(value = "客户群id")
        private String groupChatId;
    }
}
