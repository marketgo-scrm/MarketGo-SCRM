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
 * @date :  2022-07-05 20:28:25
 * @description : LoginUserRequest.java
 */
@Data
@Builder
@ApiModel
public class RolePermissionsResponse {
    @ApiModelProperty(name = "permissionsUuid", notes = "功能权限UUID", example = "")
    private String permissionsUuid;
    @ApiModelProperty(name = "parentCode", notes = "父级权限code", example = "")
    private String parentCode;
    @ApiModelProperty(name = "name", notes = "功能权限名称", example = "")
    private String name;
    @ApiModelProperty(name = "code", notes = "功能权限编码", example = "")
    private String code;
    @ApiModelProperty(name = "title", notes = "功能权限标题", example = "")
    private String title;
    @ApiModelProperty(name = "status", notes = "启用状态", example = "")
    private Boolean status;
    @ApiModelProperty(name = "children", notes = "子权限列表", example = "")
    private List<RolePermissionsResponse> children = Lists.newArrayList();


}
