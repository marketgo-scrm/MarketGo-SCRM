package com.easy.marketgo.web.model.response.corp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 10:43 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微配置域名信息response")
public class WeComCorpDomainResponse {
    @ApiModelProperty(value = "域名信息")
    private String domainUrl;

    @ApiModelProperty(value = "可信文件名称")
    private String credFileName;
}
