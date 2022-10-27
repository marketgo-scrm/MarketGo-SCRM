package com.easy.marketgo.web.model.response.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel
public class UserRoleInfo {
    @ApiModelProperty(name = "code", notes = "角色code", example = "")
    private String code;
    @ApiModelProperty(name = "desc", notes = "角色描述/名称", example = "")
    private String desc;
    @ApiModelProperty(name = "roleUuid", notes = "角色uuid", example = "")
    private String roleUuid;
    @ApiModelProperty(name = "count", notes = "角色成员数量", example = "")
    private long count;
}
