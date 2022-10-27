package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/20/22 2:51 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户群群主response")
public class WeComGroupChatOwnersResponse {
    @ApiModelProperty(value = "企业的客户群群主详情")
    private List<GroupChatOwner> groupChatOwners;

    @Data
    public static class GroupChatOwner {
        @ApiModelProperty(value = "企业的客户群群主名称")
        private String ownerName;
        @ApiModelProperty(value = "企业的客户群群主ID")
        private String ownerId;
    }
}
