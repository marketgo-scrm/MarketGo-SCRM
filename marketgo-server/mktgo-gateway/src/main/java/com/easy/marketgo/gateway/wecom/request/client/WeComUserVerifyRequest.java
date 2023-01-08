package com.easy.marketgo.gateway.wecom.request.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/3/23 11:47 PM
 * Describe:
 */
@Data
@ApiModel(description = "用户登录验证的request")
public class WeComUserVerifyRequest {
    @ApiModelProperty(value = "验证的code")
    private String code;
    @ApiModelProperty(value = "验证的员工id")
    private String memberId;
}
