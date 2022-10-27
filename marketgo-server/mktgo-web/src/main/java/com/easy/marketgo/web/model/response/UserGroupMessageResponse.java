package com.easy.marketgo.web.model.response;

import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 6:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "人群信息response")
public class UserGroupMessageResponse {

    @ApiModelProperty(value = "人群预估条件的uuid")
    private String uuid;


    /**
     * 预估计算的状态, COMPUTING 正在计算，SUCCEED 计算成功，FAILED 计算失败
     */
    @ApiModelProperty(value = "人群计算的状态. COMPUTING 正在计算，SUCCEED 计算成功，FAILED 计算失败")
    private String status;
    @ApiModelProperty(value = "人群的员工数量")
    private int memberCount;
    @ApiModelProperty(value = "人群的客户数量")
    private int externalUserCount;
    @ApiModelProperty(value = "人群的信息")
    private UserGroupAudienceRules userGroup;
}
