package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/18/22 5:29 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微客户群response")
public class WeComGroupChatsResponse {
    @ApiModelProperty(value = "企业的客户群总数")
    private Integer totalCount;
    @ApiModelProperty(value = "企业的客户群详情")
    private List<GroupChat> groupChats;

    @Data
    @ApiModel(description = "企微客户群详情")
    public static class GroupChat {
        @ApiModelProperty(value = "企业的客户群名称")
        private String groupChatName;
        @ApiModelProperty(value = "企业的客户群id")
        private String groupChatId;
        @ApiModelProperty(value = "企业的客户群成员总数")
        private Integer count;
        @ApiModelProperty(value = "企业的客户群群主名称")
        private String ownerName;
        @ApiModelProperty(value = "企业的客户群群主ID")
        private String ownerId;
        @ApiModelProperty(value = "客户群创建时间")
        private String createTime;
    }
}
