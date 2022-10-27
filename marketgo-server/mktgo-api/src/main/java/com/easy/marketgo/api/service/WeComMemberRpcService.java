package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.customer.QueryMemberDetailClientRequest;
import com.easy.marketgo.api.model.request.customer.QueryMembersClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryDepartmentsRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/30/22 12:29 PM
 * Describe:
 */
public interface WeComMemberRpcService {
    RpcResponse queryDepartments(WeComQueryDepartmentsRequest request);
    RpcResponse queryDepartmentDetail(WeComQueryDepartmentsRequest request);
    RpcResponse queryDepartmentMembers(WeComQueryDepartmentsRequest request);
    RpcResponse queryMembers(QueryMembersClientRequest request);
    RpcResponse queryMemberDetail(QueryMemberDetailClientRequest request);
}
