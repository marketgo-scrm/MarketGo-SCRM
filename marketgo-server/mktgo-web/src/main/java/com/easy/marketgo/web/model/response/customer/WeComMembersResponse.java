package com.easy.marketgo.web.model.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/21/22 4:05 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微员工列表response")
public class WeComMembersResponse {
    @ApiModelProperty(value = "企业的员工总数")
    private Integer totalCount;
    @ApiModelProperty(value = "企业的员工详情列表")
    private List<MemberMessage> members;

    @Data
    public static class MemberMessage {
        private Boolean isMember;
        @ApiModelProperty(value = "企业员工名称")
        private String memberName;
        @ApiModelProperty(value = "企业员工ID")
        private String memberId;
    }
}
