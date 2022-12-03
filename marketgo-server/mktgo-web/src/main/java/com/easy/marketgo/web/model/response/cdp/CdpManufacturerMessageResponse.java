package com.easy.marketgo.web.model.response.cdp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 4:17 PM
 * Describe:
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "CDP信息request")
public class CdpManufacturerMessageResponse {
    @ApiModelProperty(value = "cdp的id")
    private Integer id;
    @ApiModelProperty(value = "CDP的API访问地址")
    private String apiUrl;
    @ApiModelProperty(value = "CDP的appKey")
    private String appKey;
    @ApiModelProperty(value = "项目的英文名称")
    private String projectName;
    @ApiModelProperty(value = "CDP的apiSecret")
    private String apiSecret;
    @ApiModelProperty(value = "事件数据上报的地址")
    private String dataUrl;
}
