package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/5/22 5:46 PM
 * Describe:
 */
@Data
public class QueryMemberDetailResponse extends WeComBaseResponse {
    @JsonProperty("userid")
    private String userId;
    private String name;
    private List<Long> department;
    private List<Long> order;
    private Integer status;
}
