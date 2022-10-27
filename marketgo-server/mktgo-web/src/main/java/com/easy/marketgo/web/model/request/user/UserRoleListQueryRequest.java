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
 * @description : LoginUserRequest.java
 */
@Data
@Builder
@ApiModel
public class UserRoleListQueryRequest {
    @ApiModelProperty(name = "tenantUuid",notes = "租户uuid" ,example = "3OI6svzWjA24zVOp")
    @NotEmpty(message="租户uuid不能为空")
    private String  tenantUuid="3OI6svzWjA24zVOp";
    @ApiModelProperty(name = "projectUuid",notes = "项目uuid" ,example = "59I4zvzWvA24zV1p")
    @NotEmpty(message="项目uuid不能为空")
    private String  projectUuid="59I4zvzWvA24zV1p";


    @ApiModelProperty(name = "cropId",notes = "企业微信corpId" ,example = "wwa67b5f2bf5754641")
    @NotEmpty(message="企业微信CropId不能为空")
    private String  corpId="wwa67b5f2bf5754641";

    @ApiModelProperty(name = "cropId",notes = "roleUuid" ,example = "201e116a-8b37-1d53-5800-6f2928c15570")
    @NotEmpty(message="roleUuid 不能为空")
    private String roleUuid="201e116a-8b37-1d53-5800-6f2928c15570";
    @ApiModelProperty(name = "searchMemberName",notes = "搜索成员名称" ,example = "张三")
    private String searchMemberName;
}
