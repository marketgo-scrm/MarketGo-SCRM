package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/21/22 10:42 AM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户统计详情信息response")
public class WeComCustomerStatisticDetailResponse {

    private  Integer statisticType;
    @ApiModelProperty(value = "客户群统计信息")
    private List<ExternalUserStatistic> detail;

    @Data
    public static class ExternalUserStatistic {

        @ApiModelProperty(value = "企业的客户数")
        private Integer todayCount;

        private String todayTime;
    }
}
