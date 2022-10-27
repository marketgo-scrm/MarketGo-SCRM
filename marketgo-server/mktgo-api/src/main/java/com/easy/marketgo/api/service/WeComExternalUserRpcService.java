package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.tag.WeComEditExternalUserCorpTagClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUserDetailClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUsersForMemberClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/30/22 12:28 PM
 * Describe:
 */
public interface WeComExternalUserRpcService {
    RpcResponse queryExternalUsersForMember(WeComQueryExternalUsersForMemberClientRequest request);

    RpcResponse queryExternalUserDetail(WeComQueryExternalUserDetailClientRequest request);

    RpcResponse editExternalUserCorpTag(WeComEditExternalUserCorpTagClientRequest request);
}
