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
@ApiModel(description = "企微客户统计信息的response")
public class WeComCustomerStatisticResponse {
    @ApiModelProperty(value = "客户群数量")
    private Integer groupChatsCount;
    @ApiModelProperty(value = "今天客户群入群数量")
    private Integer todayJoinGroupCount;
    @ApiModelProperty(value = "今天客户群退群数量")
    private Integer todayQuitGroupCount;
    @ApiModelProperty(value = "客户群更新时间")
    private String groupChatsUpdateTime;

    @ApiModelProperty(value = "客户数量")
    private Integer externalUserCount;
    @ApiModelProperty(value = "今天新增客户数量")
    private Integer todayNewExternalUserCount;
    @ApiModelProperty(value = "今天流失客户数量")
    private Integer todayLossExternalUserCount;
    @ApiModelProperty(value = "客户更新时间")
    private String ExternalUserUpdateTime;
}
