package com.easy.marketgo.web.model.response.cdp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

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
@ApiModel(description = "CDP list信息response")
public class CdpManufactureListResponse {
    @ApiModelProperty(value = "分群的list")
    private List<CdpManufactureMessage> cdpList;


    @Data
    public static class CdpManufactureMessage {

        @ApiModelProperty(value = "cdp的名称")
        private String cdpName;

        @ApiModelProperty(value = "cdp的类型")
        private String cdpType;

        @ApiModelProperty(value = "配置的状态")
        private Boolean configStatus;

        @ApiModelProperty(value = "开启的状态")
        private Boolean switchStatus;

        @ApiModelProperty(value = "CDP的描述信息")
        private String desc;
    }
}
