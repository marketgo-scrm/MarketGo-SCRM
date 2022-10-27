package com.easy.marketgo.api.model.request.customer;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 4:43 PM
 * Describe:
 */
@Data
public class WeComQueryExternalUserDetailClientRequest extends BaseRpcRequest {
    private String externalUserId;
    private String nextCursor;
}
