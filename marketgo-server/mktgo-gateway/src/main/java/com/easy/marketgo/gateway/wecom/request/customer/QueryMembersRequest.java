package com.easy.marketgo.gateway.wecom.request.customer;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 11:29 PM
 * Describe:
 */
@Data
public class QueryMembersRequest {
    private String cursor;
    private Integer limit;
}
