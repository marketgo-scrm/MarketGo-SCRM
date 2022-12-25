package com.easy.marketgo.web.model.response.cdp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/21/22 3:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "CDP的开启状态response")
public class CdpSwitchStatusResponse {

    @ApiModelProperty(value = "cdp的开启状态")
    private Boolean switchStatus;

}
