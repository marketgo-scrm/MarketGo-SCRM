package com.easy.marketgo.web.model.response.contactway;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
@ApiModel(description = "活码创建请求")
public class ChannelLiveCodeStatisticsResponse {



    @ApiModelProperty(name = "id", notes = "活码ID")
    private int id;
    @ApiModelProperty(name = "title", notes = "活码名称")
    private String title;
    @ApiModelProperty(name = "统计详情", notes = "统计详情")
    private List<ContactWayStatistics> detail;
    @ApiModelProperty(name = "total", notes = "总记录数", example = "2000")
    private int total;
    @Data
    @ApiModel(description = "统计汇总信息")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public  static  class  ContactWayStatistics{
        @ApiModelProperty(name = "addExtUserDate", notes = "添加客户日期")
        private String addExtUserDate;
        @ApiModelProperty(name = "extUserCount", notes = "客户总数")
        private long extUserCount;
        @ApiModelProperty(name = "increasedExtUserCount", notes = "新增客户数")
        private long increasedExtUserCount;
        @ApiModelProperty(name = "decreaseExtUserCount", notes = "流失客户数")
        private long decreaseExtUserCount;

        @ApiModelProperty(name = "memberId", notes = "员工ID")
        private String memberId;
        @ApiModelProperty(name = "memberName", notes = "员工名称")
        private String memberName;
        @ApiModelProperty(name = "thumbAvatar", notes = "员工头像")
        private String thumbAvatar;
    }


}
