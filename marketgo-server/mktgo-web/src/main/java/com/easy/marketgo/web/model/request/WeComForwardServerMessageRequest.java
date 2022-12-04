package com.easy.marketgo.web.model.request;

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
@ApiModel(description = "企微配置转发服务信息request")
public class WeComForwardServerMessageRequest {
    @ApiModelProperty(value = "微配置转发服务信息")
    private List<String> forwardServer;
}
