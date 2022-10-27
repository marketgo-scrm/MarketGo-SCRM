package com.easy.marketgo.web.model.request.user;

import com.easy.marketgo.common.enums.PermissionsEnum;
import com.easy.marketgo.web.model.response.user.RolePermissionsResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:28:25
 * @description : RolePermissionsRequest.java
 */
@Data
@Builder
@ApiModel
public class RolePermissionsAuthorizationRequest {

    @ApiModelProperty(name = "tenantUuid", notes = "租户uuid", example = "3OI6svzWjA24zVOp")
    @NotEmpty(message = "租户uuid不能为空")
    private String tenantUuid = "3OI6svzWjA24zVOp";
    @ApiModelProperty(name = "projectUuid", notes = "项目uuid", example = "59I4zvzWvA24zV1p")
    @NotEmpty(message = "项目uuid不能为空")
    private String projectUuid = "59I4zvzWvA24zV1p";
    @ApiModelProperty(name = "cropId", notes = "企业微信corpId", example = "wwa67b5f2bf5754641")
    @NotEmpty(message = "企业微信CropId不能为空")
    private String corpId = "wwa67b5f2bf5754641";
    @ApiModelProperty(name = "roleUuid", notes = "roleUuid", dataType = "string",example = "1111111111")
    @NotEmpty(message="角色id不能为空")
    private  String roleUuid;
    @ApiModelProperty(name = "permissions", notes = "permissions",example = "")
    private List<RolePermissionsInfo> permissions;


    @Data
    @ApiModel
    public static class RolePermissionsInfo {
        private String permissionsUuid;
        private String name;
        private String code;
        private String title;
        private Boolean status;
        private List<RolePermissionsInfo> children = Lists.newArrayList();


    }


}
