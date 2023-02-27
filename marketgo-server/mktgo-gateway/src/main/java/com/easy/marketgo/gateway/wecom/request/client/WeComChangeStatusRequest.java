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
@ApiModel(description = "用户状态修改的request")
public class WeComChangeStatusRequest {
    @ApiModelProperty(value = "修改的状态的类型， MEMBER 员工， EXTERNAL_USER 客户")
    private String type;
    @ApiModelProperty(value = "员工id")
    private String memberId;
    @ApiModelProperty(value = "客户ID")
    private String externalUserId;
    @ApiModelProperty(value = "任务uuid")
    String taskUuid;
    @ApiModelProperty(value = "任务组uuid")
    String uuid;
    @ApiModelProperty(value = "任务执行的时间")
    String sentTIme;
    @ApiModelProperty(value = "状态")
    String status;
}
