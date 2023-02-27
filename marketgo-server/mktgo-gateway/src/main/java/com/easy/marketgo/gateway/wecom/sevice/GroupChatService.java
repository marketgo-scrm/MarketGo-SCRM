package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.chats.WeComQueryGroupChatClientResponse;
import com.easy.marketgo.api.model.response.chats.WeComQueryGroupChatMembersClientResponse;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.customer.QueryGroupChatMembersRequest;
import com.easy.marketgo.gateway.wecom.request.customer.QueryGroupChatRequest;
import com.easy.marketgo.gateway.wecom.response.customer.QueryGroupChatMembersResponse;
import com.easy.marketgo.gateway.wecom.response.customer.QueryGroupChatResponse;
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
 * @data : 7/16/22 11:35 PM
 * Describe:
 */
@Slf4j
@Service
public class GroupChatService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse queryGroupChats(String corpId, String agentId, QueryGroupChatRequest request) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to group chat list, params can not be empty. corpId={}, agentId={},memberId={}",
                    corpId, agentId);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        WeComQueryGroupChatClientResponse clientResponse = new WeComQueryGroupChatClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("query group chat list.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_GROUP_CHAT_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to query group chat list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("query group chat list response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryGroupChatResponse queryGroupChatResponse = JsonUtils.toObject(response,
                    QueryGroupChatResponse.class);
            List<QueryGroupChatResponse.GroupChatListMessage> groupChats = queryGroupChatResponse.getGroupChatList();
            rpcResponse.setCode(queryGroupChatResponse.getErrcode());
            rpcResponse.setMessage(queryGroupChatResponse.getErrmsg());
            if (CollectionUtils.isNotEmpty(groupChats)) {
                ArrayList<WeComQueryGroupChatClientResponse.GroupChatListMessage> groupChatList = Lists.newArrayList();
                for (QueryGroupChatResponse.GroupChatListMessage item : groupChats) {
                    WeComQueryGroupChatClientResponse.GroupChatListMessage message =
                            new WeComQueryGroupChatClientResponse.GroupChatListMessage();
                    BeanUtils.copyProperties(item, message);
                    groupChatList.add(message);
                }
                clientResponse.setGroupChatList(groupChatList);
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query group chat list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query group chat list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }


    public RpcResponse queryGroupChatMembers(String corpId, String agentId, QueryGroupChatMembersRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to group chat member list, params can not be empty. corpId={}, agentId={},memberId={}",
                    corpId, agentId);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        WeComQueryGroupChatMembersClientResponse clientResponse = new WeComQueryGroupChatMembersClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("query group chat member list.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_GROUP_CHAT_MEMBER_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to query group chat member list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("query group chat member list response from weCom. response={}", StringUtils.isBlank(response) ?
                    "" :
                    response);
            QueryGroupChatMembersResponse queryGroupChatMembersResponse = JsonUtils.toObject(response,
                    QueryGroupChatMembersResponse.class);
            WeComQueryGroupChatMembersClientResponse.GroupChatMessage groupChat =
                    new WeComQueryGroupChatMembersClientResponse.GroupChatMessage();
            BeanUtils.copyProperties(queryGroupChatMembersResponse.getGroupChat(), groupChat);
            List<QueryGroupChatMembersResponse.MemberListMessage> groupChatMembers =
                    queryGroupChatMembersResponse.getGroupChat().getMemberList();
            rpcResponse.setCode(queryGroupChatMembersResponse.getErrcode());
            rpcResponse.setMessage(queryGroupChatMembersResponse.getErrmsg());
            ArrayList<WeComQueryGroupChatMembersClientResponse.MemberListMessage> groupChatMemberList =
                    Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(groupChatMembers)) {
                for (QueryGroupChatMembersResponse.MemberListMessage item : groupChatMembers) {
                    WeComQueryGroupChatMembersClientResponse.MemberListMessage message =
                            new WeComQueryGroupChatMembersClientResponse.MemberListMessage();
                    BeanUtils.copyProperties(item, message);
                    groupChatMemberList.add(message);
                }
            }
            groupChat.setMemberList(groupChatMemberList);


            List<QueryGroupChatMembersResponse.InvitorMessage> groupChatAdmins =
                    queryGroupChatMembersResponse.getGroupChat().getAdminList();
            ArrayList<WeComQueryGroupChatMembersClientResponse.InvitorMessage> groupChatAdminList =
                    Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(groupChatMembers)) {
                for (QueryGroupChatMembersResponse.InvitorMessage item : groupChatAdmins) {
                    WeComQueryGroupChatMembersClientResponse.InvitorMessage message =
                            new WeComQueryGroupChatMembersClientResponse.InvitorMessage();
                    BeanUtils.copyProperties(item, message);
                    groupChatAdminList.add(message);
                }
            }
            groupChat.setAdminList(groupChatAdminList);
            clientResponse.setGroupChat(groupChat);
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query group chat list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query group chat list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
