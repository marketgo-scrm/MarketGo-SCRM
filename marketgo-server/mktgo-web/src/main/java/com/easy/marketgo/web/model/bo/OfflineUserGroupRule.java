package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 7:50 PM
 * Describe:
 */
@Data
@ApiModel(description = "离线人群计算的条件详情")
public class OfflineUserGroupRule {
    @ApiModelProperty(value = "人群条件uuid")
    private String userGroupUuid;
}
