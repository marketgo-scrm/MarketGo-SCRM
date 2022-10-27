package com.easy.marketgo.web.model.request;

import com.easy.marketgo.web.model.bo.CdpUserGroupRule;
import com.easy.marketgo.web.model.bo.OfflineUserGroupRule;
import com.easy.marketgo.web.model.bo.WeComUserGroupRule;
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
@ApiModel(description = "人群计算的条件request")
public class UserGroupAudienceRules {
    /**
     * 企微 wecom
     * 通过数据仓库 cdp
     * 离线   offline
     */
    @ApiModelProperty(value = "人群的类型 企微 WECOM， 数据仓库 CDP, 离线 OFFLINE", allowableValues = "WECOM, CDP, OFFLINE",
            example="WECOM")
    private String userGroupType;
    @ApiModelProperty(value = "企微人群的计算条件")
    private WeComUserGroupRule weComUserGroupRule;
    @ApiModelProperty(value = "数据仓库人群的计算条件")
    private CdpUserGroupRule cdpUserGroupRule;
    @ApiModelProperty(value = "离线人群的计算条件")
    private OfflineUserGroupRule offlineUserGroupRule;
    @ApiModelProperty(value = "人群的计算条件关系。 OR或  AND并且")
    private String relation;
}
