package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/21/22 10:42 AM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户群统计和群描述信息response")
public class WeComGroupChatStatisticResponse {
    @ApiModelProperty(value = "企业的客户群名称")
    private String groupChatName;
    @ApiModelProperty(value = "企业的客户群id")
    private String groupChatId;
    @ApiModelProperty(value = "企业的客户群群主名称")
    private String ownerName;
    @ApiModelProperty(value = "企业的客户群群主ID")
    private String ownerId;
    @ApiModelProperty(value = "客户群公告")
    private String notice;
    @ApiModelProperty(value = "客户群创建时间")
    private String createTime;
    @ApiModelProperty(value = "客户群统计信息")
    private GroupChatStatistic statistic;

    @Data
    public static class GroupChatStatistic {
        @ApiModelProperty(value = "企业的客户群成员总数")
        private Integer totalCount;

        @ApiModelProperty(value = "企业的客户群今天入群人数")
        private Integer todayAddCount;

        @ApiModelProperty(value = "企业的客户群今天退群人数")
        private Integer todayExitCount;
    }

}
