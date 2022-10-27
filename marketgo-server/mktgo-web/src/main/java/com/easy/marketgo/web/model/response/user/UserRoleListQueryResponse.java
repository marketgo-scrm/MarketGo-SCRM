package com.easy.marketgo.web.model.response.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 17:23:37
 * @description : UserRoleListQueryResponse.java
 */
@Data
@Builder
@ApiModel
public class UserRoleListQueryResponse {
    @ApiModelProperty(name = "infos", notes = "角色成员列表", example = "")
    private List<WeComMemberInfo> infos;

    @Data
    @Builder
    public static class WeComMemberInfo {
        @ApiModelProperty(name = "memberId", notes = "成员id", example = "")
        private String memberId;
        @ApiModelProperty(name = "memberName", notes = "成员名称", example = "")
        private String memberName;
        @ApiModelProperty(name = "corpId", notes = "企业id", example = "")
        private String corpId;
        @ApiModelProperty(name = "id", notes = "部门id", example = "")
        private String departmentId;
        @ApiModelProperty(name = "departmentName", notes = "部门名称", example = "")
        private String departmentName;
        @ApiModelProperty(name = "mainDepartmentId", notes = "主部门id", example = "")
        private String mainDepartmentId;
        @ApiModelProperty(name = "mainDepartmentName", notes = "主部门名称", example = "")
        private String mainDepartmentName;
        @ApiModelProperty(name = "thumbAvatar", notes = "缩略图url", example = "")
        private String thumbAvatar;
        @ApiModelProperty(name = "avatar", notes = "图url", example = "")
        private String avatar;
        @ApiModelProperty(name = "externalUserCount", notes = "客户数", example = "2")
        private String externalUserCount;

    }

}
