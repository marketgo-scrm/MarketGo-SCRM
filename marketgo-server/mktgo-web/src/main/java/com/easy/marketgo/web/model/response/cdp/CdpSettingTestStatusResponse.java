package com.easy.marketgo.web.model.response.cdp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/21/22 3:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "CDP的Setting测试状态response")
public class CdpSettingTestStatusResponse {

    @ApiModelProperty(value = "cdp的测试状态")
    private Boolean status;

    @ApiModelProperty(value = "cdp的测试状态描述")
    private String message;
}
