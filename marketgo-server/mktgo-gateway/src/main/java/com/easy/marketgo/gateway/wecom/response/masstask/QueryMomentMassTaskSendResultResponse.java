package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:19 PM
 * Describe:
 */


@Data
public class QueryMomentMassTaskSendResultResponse extends WeComBaseResponse {
    @JsonProperty(value = "next_cursor")
    private String nextCursor;
    @JsonProperty(value = "customer_list")
    private List<CustomerListMessage> customerList;


    @Data
    public static class CustomerListMessage {
        @JsonProperty(value = "external_userid")
        private String externalUserId;
    }
}
