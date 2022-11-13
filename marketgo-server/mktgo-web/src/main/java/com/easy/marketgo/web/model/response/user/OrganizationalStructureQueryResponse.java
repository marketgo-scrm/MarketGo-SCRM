package com.easy.marketgo.web.model.response.user;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 20:05:09
 * @description : OrganizationalStructureResponse.java
 */
@Data
@Builder
@ApiModel
public class OrganizationalStructureQueryResponse {
    @ApiModelProperty(name = "members", notes = "列表list", example = "")
    private List<OrganizationalStructureMember> members = Lists.newArrayList();

    @Data
    @ApiModel
    public static class OrganizationalStructureMember {

        @ApiModelProperty(name = "memberId", notes = "成员id", example = "")
        private String memberId;
        @ApiModelProperty(name = "memberName", notes = "成员名称", example = "")
        private String memberName;
        private String corpId;
        @ApiModelProperty(name = "id", notes = "部门", example = "")
        private String departmentId;
        @ApiModelProperty(name = "departmentName", notes = "部门名称", example = "")
        private String departmentName;
        @ApiModelProperty(name = "leaderDepartmentId", notes = "领导部门id", example = "")
        private String leaderDepartmentId;
        @ApiModelProperty(name = "leaderDepartmentName", notes = "领导部门名称", example = "")
        private String leaderDepartmentName;

        @ApiModelProperty(name = "thumbAvatar", notes = "缩略图", example = "")
        private String thumbAvatar;
        @ApiModelProperty(name = "avatar", notes = "头像", example = "")
        private String avatar;
        @ApiModelProperty(name = "roleCode", notes = "角色code", example = "administrator")
        private String roleCode;
        @ApiModelProperty(name = "roleDesc", notes = "角色名称", example = "管理员")
        private String roleDesc;
        @ApiModelProperty(name = "mobile", notes = "手机号")
        private String mobile;
        @ApiModelProperty(name = "authStatus", notes = "授权状态")
        private Boolean authStatus;
        @ApiModelProperty(name = "externalUserCount", notes = "客户数", example = "2")
        private String externalUserCount;
        @ApiModelProperty(name = "authorizationStatus",notes = "授权状态")
        private boolean authorizationStatus;
        @ApiModelProperty(name = "roleUuid", notes = "角色uuid", example = "")
        private String roleUuid;

    }

}
