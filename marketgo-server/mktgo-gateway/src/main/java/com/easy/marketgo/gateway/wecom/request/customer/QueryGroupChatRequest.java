package com.easy.marketgo.gateway.wecom.request.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/16/22 11:42 PM
 * Describe:
 */
@Data
public class QueryGroupChatRequest {

    @JsonProperty("status_filter")
    private Integer statusFilter;
    @JsonProperty("owner_filter")
    private OwnerFilterMessage ownerFilter;
    private String cursor;

    private Integer limit;

    @Data
    public static class OwnerFilterMessage {
        @JsonProperty("userid_list")
        private List<String> userIdList;
    }
}
