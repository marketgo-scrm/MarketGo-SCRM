package com.easy.marketgo.web.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:38 PM
 * Describe:
 */
@Data
@ApiModel(description = "项目创建request")
public class ProjectCreateRequest {
    @ApiModelProperty(value = "任务id")
    private Integer id;
    @ApiModelProperty(value = "项目名称", required = true)
    private String name;
    @ApiModelProperty(value = "项目描述", required = true)
    private String desc;
}
