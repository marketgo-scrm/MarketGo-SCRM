package com.easy.marketgo.api.model.request.customer;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 11:29 PM
 * Describe:
 */
@Data
public class QueryMembersClientRequest extends BaseRpcRequest {
    private String cursor;
    private Integer limit;
}
