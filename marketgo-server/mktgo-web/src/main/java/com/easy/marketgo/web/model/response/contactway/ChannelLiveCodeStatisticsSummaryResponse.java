package com.easy.marketgo.web.model.response.contactway;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-23 13:46:30
 * @description : ChannelContactWayRequest.java
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "活码统计数据的response")
public class ChannelLiveCodeStatisticsSummaryResponse {

    @ApiModelProperty(name = "lastStatisticsTime", notes = "统计时间")
    private String lastStatisticsTime;
    @ApiModelProperty(name = "dailyIncreasedExtUserCount", notes = "日新增")
    private int dailyIncreasedExtUserCount;
    @ApiModelProperty(name = "dailyDecreaseExtUserCount", notes = "日流失")
    private int dailyDecreaseExtUserCount;
    @ApiModelProperty(name = "totalExtUserCount", notes = "总客户数")
    private int totalExtUserCount;
    @ApiModelProperty(name = "totalDecreaseExtUserCount", notes = "流失客户总数")
    private int totalDecreaseExtUserCount;

}
