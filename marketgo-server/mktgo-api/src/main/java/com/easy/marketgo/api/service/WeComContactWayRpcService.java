package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.contactway.WeComContactWayClientRequest;
import com.easy.marketgo.api.model.request.contactway.WeComDeleteContactWayClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 2:54 PM
 * Describe:
 */
public interface WeComContactWayRpcService {

    RpcResponse addContactWay(WeComContactWayClientRequest request);

    RpcResponse updateContactWay(WeComContactWayClientRequest request);

    RpcResponse deleteContactWay(WeComDeleteContactWayClientRequest request);
}
