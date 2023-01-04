package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 5:09 PM
 * Describe:
 */
@Data
public class QueryExternalUsersForMemberResponse extends WeComBaseResponse {
    @JsonProperty("external_userid")
    List<String> externalUserId;

}
