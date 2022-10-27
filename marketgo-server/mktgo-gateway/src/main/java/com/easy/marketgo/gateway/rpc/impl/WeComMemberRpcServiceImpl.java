package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.customer.QueryMemberDetailClientRequest;
import com.easy.marketgo.api.model.request.customer.QueryMembersClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryDepartmentsRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComMemberRpcService;
import com.easy.marketgo.gateway.wecom.request.customer.QueryDepartmentsRequest;
import com.easy.marketgo.gateway.wecom.request.customer.QueryMembersRequest;
import com.easy.marketgo.gateway.wecom.sevice.MembersManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 11:24 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComMemberRpcServiceImpl implements WeComMemberRpcService {
    @Autowired
    private MembersManagerService membersManagerService;

    @Override
    public RpcResponse queryDepartments(WeComQueryDepartmentsRequest request) {
        QueryDepartmentsRequest queryDepartmentsRequest = new QueryDepartmentsRequest();
        queryDepartmentsRequest.setId(request.getDepartmentId());
        return membersManagerService.queryDepartments(request.getCorpId(), request.getAgentId(),
                queryDepartmentsRequest);
    }

    @Override
    public RpcResponse queryDepartmentDetail(WeComQueryDepartmentsRequest request) {
        QueryDepartmentsRequest queryDepartmentsRequest = new QueryDepartmentsRequest();
        queryDepartmentsRequest.setId(request.getDepartmentId());
        return membersManagerService.queryDepartmentDetail(request.getCorpId(), request.getAgentId(),
                queryDepartmentsRequest);
    }

    @Override
    public RpcResponse queryDepartmentMembers(WeComQueryDepartmentsRequest request) {
        QueryDepartmentsRequest queryDepartmentsRequest = new QueryDepartmentsRequest();
        queryDepartmentsRequest.setId(request.getDepartmentId());
        return membersManagerService.queryDepartmentMembers(request.getCorpId(), request.getAgentId(),
                queryDepartmentsRequest);
    }

    @Override
    public RpcResponse queryMembers(QueryMembersClientRequest request) {
        QueryMembersRequest queryMembersRequest = new QueryMembersRequest();
        if (StringUtils.isNotBlank(request.getCursor())) {
            queryMembersRequest.setCursor(request.getCursor());
        }
        if (request.getLimit() != null) {
            queryMembersRequest.setLimit(request.getLimit());
        }
        return membersManagerService.queryMembers(request.getCorpId(), request.getAgentId(),
                queryMembersRequest);
    }

    @Override
    public RpcResponse queryMemberDetail(QueryMemberDetailClientRequest request) {
        return membersManagerService.queryMemberDetail(request.getCorpId(), request.getAgentId(),
                request.getUserId());
    }
}
