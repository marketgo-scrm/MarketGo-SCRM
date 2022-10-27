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
@ApiModel(description = "企微客户详情response")
public class WeComExternalUserDetailResponse {
    @ApiModelProperty(value = "企业的客户总数")
    private Integer totalCount;
    @ApiModelProperty(value = "企业的客户详情列表")
    private List<WeComMember> members;

    @Data
    public static class WeComMember {
        @ApiModelProperty(value = "企业员工名称")
        private String memberName;
        @ApiModelProperty(value = "企业员工ID")
        private String memberId;
        @ApiModelProperty(value = "头像url")
        private String avatar;
        @ApiModelProperty(value = "外部联系人性别, 0-未知 1-男性 2-女性")
        private Integer gender;
        @ApiModelProperty(value = "客户备注")
        private String remark;
        @ApiModelProperty(value = "客户手机号")
        private String mobile;
        @ApiModelProperty(value = "客户描述")
        private String description;
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
}
