package com.easy.marketgo.web.model.response.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-21 19:47:08
 * @description : UserRoleListResponse.java
 */
@Data
@Builder
@ApiModel
public class UserRoleListResponse {
    @ApiModelProperty(name = "infos", notes = "角色列表", example = "")
    private List<UserRoleInfo> infos;


}
