package com.easy.marketgo.web.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 6:03 PM
 * Describe:
 */
@Data
public class WeComCreateTaskBaseResponse {
    @ApiModelProperty(value = "创建返回uuid")
    private String uuid;
}
