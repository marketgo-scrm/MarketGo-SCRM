package com.easy.marketgo.web.model.request.contactway;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-23 13:46:30
 * @description : ChannelContactWayRequest.java
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "活码列表请求")
public class ChannelLiveCodeRequest {

    @ApiModelProperty(name = "tenantUuid", notes = "租户uuid", example = "3OI6svzWjA24zVOp")
    @NotEmpty(message = "租户uuid不能为空")
    private String tenantUuid = "3OI6svzWjA24zVOp";
    @ApiModelProperty(name = "projectUuid", notes = "项目uuid", example = "59I4zvzWvA24zV1p")
    @NotEmpty(message = "项目uuid不能为空")
    private String projectUuid = "59I4zvzWvA24zV1p";
    @ApiModelProperty(name = "cropId", notes = "企业微信corpId", example = "wwa67b5f2bf5754641")
    @NotEmpty(message = "企业微信CropId不能为空")
    private String corpId = "wwa67b5f2bf5754641";
    @ApiModelProperty(name = "queryListByPage", notes = "分页index", example = "0")
    private int pageNum =1;
    @ApiModelProperty(name = "pageSize", notes = "分页大小", example = "20")
    private int pageSize=20;
    @ApiModelProperty(name = "searchTitle", notes = "搜索的二维码名称", example = "测试活码")
    private String searchTitle;

}
