package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.customer.*;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.customer.QueryDepartmentsRequest;
import com.easy.marketgo.gateway.wecom.request.customer.QueryMembersRequest;
import com.easy.marketgo.gateway.wecom.response.customer.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 11:28 PM
 * Describe:
 */
@Slf4j
@Service
public class MembersManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse queryDepartments(String corpId, String agentId, QueryDepartmentsRequest request) {
        WeComQueryDepartmentsClientResponse clientResponse = new WeComQueryDepartmentsClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
            String departmentId = request.getId();

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put(WeComHttpConstants.DEPARTMENTS_ID, departmentId);

            log.info("query department for id. headerParams={}", JSON.toJSONString(params));
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_DEPARTMENT_LIST_URL, params);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to query department list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("query departments response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryDepartmentsResponse queryDepartmentsResponse = JsonUtils.toObject(response,
                    QueryDepartmentsResponse.class);
            List<QueryDepartmentsResponse.WeComDepartmentMessage> departments =
                    queryDepartmentsResponse.getDepartment();
            rpcResponse.setCode(queryDepartmentsResponse.getErrcode());
            rpcResponse.setMessage(queryDepartmentsResponse.getErrmsg());
            if (CollectionUtils.isNotEmpty(departments)) {
                ArrayList<WeComQueryDepartmentsClientResponse.WeComDepartmentMessage> deptList = Lists.newArrayList();
                for (QueryDepartmentsResponse.WeComDepartmentMessage item : departments) {
                    WeComQueryDepartmentsClientResponse.WeComDepartmentMessage message =
                            new WeComQueryDepartmentsClientResponse.WeComDepartmentMessage();
                    BeanUtils.copyProperties(item, message);
                    deptList.add(message);
                }
                clientResponse.setDepartment(deptList);
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query department list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query departments rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse queryDepartmentDetail(String corpId, String agentId, QueryDepartmentsRequest request) {
        QueryDepartmentDetailClientResponse clientResponse = new QueryDepartmentDetailClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
            String departmentId = request.getId();

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put(WeComHttpConstants.DEPARTMENTS_ID, departmentId);

            log.info("start to query department detail. headerParams={}", JSON.toJSONString(params));
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_DEPARTMENT_DETAIL_URL, params);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to query department detail is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("query department detail response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryDepartmentDetailResponse queryDepartmentDetailResponse = JsonUtils.toObject(response,
                    QueryDepartmentDetailResponse.class);
            rpcResponse.setCode(queryDepartmentDetailResponse.getErrcode());
            rpcResponse.setMessage(queryDepartmentDetailResponse.getErrmsg());
            if (queryDepartmentDetailResponse.getDepartment() != null) {
                QueryDepartmentDetailClientResponse.WeComDepartmentMessage message =
                        new QueryDepartmentDetailClientResponse.WeComDepartmentMessage();
                BeanUtils.copyProperties(queryDepartmentDetailResponse.getDepartment(), message);
                clientResponse.setDepartment(message);
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query department detail. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query department detail rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse queryDepartmentMembers(String corpId, String agentId, QueryDepartmentsRequest request) {
        WeComQueryDepartmentMembersClientResponse clientResponse = new WeComQueryDepartmentMembersClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
            String departmentId = request.getId();

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put(WeComHttpConstants.DEPARTMENT_MEMBER_ID, departmentId);

            log.info("query department member list for id.headerParams={}", JSON.toJSONString(params));
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_DEPARTMENT_MEMBER_LIST_URL,
                    params);
            log.info("query department member response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryDepartmentMembersResponse queryDepartmentMembersResponse = JsonUtils.toObject(response,
                    QueryDepartmentMembersResponse.class);
            if (queryDepartmentMembersResponse != null) {
                rpcResponse.setCode(queryDepartmentMembersResponse.getErrcode());
                rpcResponse.setMessage(queryDepartmentMembersResponse.getErrmsg());
                List<QueryDepartmentMembersResponse.WeComMemberMessage> memberList =
                        queryDepartmentMembersResponse.getUserList();

                if (CollectionUtils.isNotEmpty(memberList)) {
                    ArrayList<WeComQueryDepartmentMembersClientResponse.WeComMemberMessage> members =
                            Lists.newArrayList();
                    for (QueryDepartmentMembersResponse.WeComMemberMessage item : memberList) {
                        WeComQueryDepartmentMembersClientResponse.WeComMemberMessage message =
                                new WeComQueryDepartmentMembersClientResponse.WeComMemberMessage();
                        BeanUtils.copyProperties(item, message);
                        members.add(message);
                    }
                    clientResponse.setUserList(members);
                }
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query department member list. corpId={}, request={}", corpId, request, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query department members rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse queryMembers(String corpId, String agentId, QueryMembersRequest request) {
        QueryMembersClientResponse clientResponse = new QueryMembersClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("start to query member list.headerParams={}", JSON.toJSONString(params));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MEMBER_LIST_URL,
                    params, JsonUtils.toJSONString(request));
            log.info("query query member list response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryMembersResponse queryMembersResponse = JsonUtils.toObject(response, QueryMembersResponse.class);
            if (queryMembersResponse != null) {
                rpcResponse.setCode(queryMembersResponse.getErrcode());
                rpcResponse.setMessage(queryMembersResponse.getErrmsg());
                List<QueryMembersResponse.DepartmentMember> memberList = queryMembersResponse.getDeptUser();
                if (StringUtils.isNotBlank(queryMembersResponse.getNextCursor())) {
                    clientResponse.setNextCursor(queryMembersResponse.getNextCursor());
                }
                if (CollectionUtils.isNotEmpty(memberList)) {
                    ArrayList<QueryMembersClientResponse.DepartmentMember> members =
                            Lists.newArrayList();
                    for (QueryMembersResponse.DepartmentMember item : memberList) {
                        QueryMembersClientResponse.DepartmentMember message =
                                new QueryMembersClientResponse.DepartmentMember();
                        BeanUtils.copyProperties(item, message);
                        members.add(message);
                    }
                    clientResponse.setDeptUser(members);
                }
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query member list. corpId={}, request={}", corpId, request, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query member list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse queryMemberDetail(String corpId, String agentId, String userId) {
        log.info("start to query member detail. corpId={}, agentId={}, userId={}", corpId, agentId, userId);
        QueryMemberDetailClientResponse clientResponse = new QueryMemberDetailClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put("userid", userId);
            log.info("query member detail.headerParams={}", JSON.toJSONString(params));
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_MEMBER_DETAIL_URL,
                    params);
            log.info("query query member detail response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryMemberDetailResponse queryMemberDetailResponse = JsonUtils.toObject(response,
                    QueryMemberDetailResponse.class);
            if (queryMemberDetailResponse != null) {
                rpcResponse.setCode(queryMemberDetailResponse.getErrcode());
                rpcResponse.setMessage(queryMemberDetailResponse.getErrmsg());
                clientResponse.setUserId(queryMemberDetailResponse.getUserId());
                clientResponse.setName(queryMemberDetailResponse.getName());
                clientResponse.setStatus(queryMemberDetailResponse.getStatus());
                if (CollectionUtils.isNotEmpty(queryMemberDetailResponse.getDepartment())) {
                    clientResponse.setDepartment(queryMemberDetailResponse.getDepartment());
                }
                if (CollectionUtils.isNotEmpty(queryMemberDetailResponse.getOrder())) {
                    clientResponse.setOrder(queryMemberDetailResponse.getOrder());
                }
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query member detail. corpId={}, userId={}", corpId, userId, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query member detail rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
