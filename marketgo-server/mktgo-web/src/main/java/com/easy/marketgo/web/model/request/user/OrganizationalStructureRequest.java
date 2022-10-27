package com.easy.marketgo.web.model.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-05 20:28:25
 * @description : RolePermissionsRequest.java
 */
@Data
@Builder
@ApiModel
public class OrganizationalStructureRequest {

    @ApiModelProperty(name = "tenantUuid", notes = "租户uuid", example = "3OI6svzWjA24zVOp")
    @NotEmpty(message = "租户uuid不能为空")
    private String tenantUuid = "3OI6svzWjA24zVOp";
    @ApiModelProperty(name = "projectUuid", notes = "项目uuid", example = "59I4zvzWvA24zV1p")
    @NotEmpty(message = "项目uuid不能为空")
    private String projectUuid = "59I4zvzWvA24zV1p";
    @ApiModelProperty(name = "cropId", notes = "企业微信corpId", example = "wwa67b5f2bf5754641")
    @NotEmpty(message = "企业微信CropId不能为空")
    private String corpId = "wwa67b5f2bf5754641";

}
