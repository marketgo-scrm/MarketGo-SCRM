package com.easy.marketgo.web.model.response.corp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/15/22 1:41 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微配置转发服务信息response")
public class WeComForwardServerMessageResponse {
    @ApiModelProperty(value = "配置转发服务信息")
    private List<String> forwardServer;
}
