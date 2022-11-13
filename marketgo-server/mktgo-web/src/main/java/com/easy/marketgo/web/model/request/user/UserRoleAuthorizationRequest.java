package com.easy.marketgo.web.model.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:28:25
 * @description : LoginUserRequest.java
 */
@Data
@Builder
@ApiModel
public class UserRoleAuthorizationRequest {

    @ApiModelProperty(name = "tenantUuid", notes = "租户uuid", example = "3OI6svzWjA24zVOp")
    @NotEmpty(message = "租户uuid不能为空")
    private String tenantUuid = "3OI6svzWjA24zVOp";
    @ApiModelProperty(name = "projectUuid", notes = "项目uuid", example = "59I4zvzWvA24zV1p")
    @NotEmpty(message = "项目uuid不能为空")
    private String projectUuid = "59I4zvzWvA24zV1p";
    @ApiModelProperty(name = "cropId", notes = "企业微信corpId", example = "wwa67b5f2bf5754641")
    @NotEmpty(message = "企业微信CropId不能为空")
    private String corpId = "wwa67b5f2bf5754641";
    @ApiModelProperty(name = "changeRoleUserInfos", notes = "需要批量更新的用户角色请求信息",example = "")
    @NotNull(message = "需要批量更新的用户角色请求信息不能为空")
    private List<RoleUserInfo> changeRoleUserInfos;

    @Data
    @Builder
    @ApiModel
    public static class RoleUserInfo {
        @ApiModelProperty(name = "targetRoleUuid", notes = "targetRoleUuid 要授予的用户角色id", dataType = "",example = "targetRoleUuid" ,required = true)
        private String targetRoleUuid;
        @ApiModelProperty(name = "memberId", notes = "memberId 企业成员id", dataType = "",example = "shangshikun" ,required = true)
        private String memberId;
    }
}
