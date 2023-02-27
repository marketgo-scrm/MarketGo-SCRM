package com.easy.marketgo.web.model.response.corp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/15/22 1:41 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微配置侧边栏信息response")
public class WeComSidebarMessageResponse {
    @ApiModelProperty(value = "配置侧边栏信息")
    private String sidebarUrl;
}
