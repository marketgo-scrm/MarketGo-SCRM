package com.easy.marketgo.web.model.response.cdp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/21/22 3:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "CDP人群List结果response")
public class CdpCrowdListResponse {

    @ApiModelProperty(value = "分群的list")
    private List<CrowdMessage> crowds;

    @ApiModelProperty(value = "cdp的类型")
    private String cdpType;

    @Data
    public static class CrowdMessage {
        /**
         * 分群code 在查询需要用到分群时都通过code来筛选
         */
        @ApiModelProperty(value = "分群code")
        private String code;

        /**
         * 分群名称
         */
        @ApiModelProperty(value = "分群名称")
        private String name;

        /**
         * 动态分群 1为动态分群 0为静态分群
         */
        @ApiModelProperty(value = "动态分群.1为动态分群 0为静态分群")
        private String dynamic;

        /**
         * 当前分群的用户数，全部用户默认不计算
         */
        @ApiModelProperty(value = "当前分群的用户数.")
        private String userCount;
    }
}
