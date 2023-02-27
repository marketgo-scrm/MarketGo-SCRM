package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComQueryExternalUsersForMemberClientResponse;
import com.easy.marketgo.api.model.response.customer.WeComQueryExternalUserDetailClientResponse;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.EditExternalUserCorpTagRequest;
import com.easy.marketgo.gateway.wecom.response.customer.QueryExternalUserDetailResponse;
import com.easy.marketgo.gateway.wecom.response.customer.QueryExternalUsersForMemberResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 4:50 PM
 * Describe:
 */
@Slf4j
@Service
public class ExternalUserManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse getExternalUsersForMember(String corpId, String agentId, String memberId) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || StringUtils.isBlank(memberId)) {
            log.error(
                    "fail to get external user list, params can not be empty. corpId={}, agentId={},memberId={}",
                    corpId, agentId, memberId);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComQueryExternalUsersForMemberClientResponse clientResponse =
                new WeComQueryExternalUsersForMemberClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        try {
            Map<String, String> params = Maps.newHashMap();
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId,
                    Constants.AGENT_KEY_FOR_EXTERNALUSER);
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put("userid", memberId);
            log.info("start get external user list for member. paramsHeader={}, url={}", params,
                    WeComHttpConstants.QUERY_EXTERNALUSER_LIST_FOR_MEMBER_URL);
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_EXTERNALUSER_LIST_FOR_MEMBER_URL,
                    params);
            log.info("query external user list response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            if (StringUtils.isNotBlank(response)) {
                QueryExternalUsersForMemberResponse queryExternalUsersForMemberResponse =
                        JsonUtils.toObject(response, QueryExternalUsersForMemberResponse.class);
                rpcResponse.setCode(queryExternalUsersForMemberResponse.getErrcode());
                rpcResponse.setMessage(queryExternalUsersForMemberResponse.getErrmsg());
                if (!CollectionUtils.isEmpty(queryExternalUsersForMemberResponse.getExternalUserId())) {
                    clientResponse.setExternalUserId(queryExternalUsersForMemberResponse.getExternalUserId());
                }

            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query external user list. corpId={}, agentId={},memberId={}", corpId, agentId, memberId
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query external user list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse getExternalUserDetail(String corpId, String agentId, String externalUserId, String cursor) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || StringUtils.isBlank(externalUserId)) {
            log.error(
                    "fail to get external user detail, params can not be empty. corpId={}, agentId={}," +
                            "externalUserId={}",
                    corpId, agentId, externalUserId);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComQueryExternalUserDetailClientResponse clientResponse =
                new WeComQueryExternalUserDetailClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        try {
            Map<String, String> params = Maps.newHashMap();
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId,
                    Constants.AGENT_KEY_FOR_EXTERNALUSER);
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            params.put("external_userid", externalUserId);
            if (StringUtils.isNotBlank(cursor)) {
                params.put("cursor", cursor);
            }
            log.info("start get external user detail. paramsHeader={}, url={}", params,
                    WeComHttpConstants.QUERY_EXTERNALUSER_DETAIL_URL);
            String response = null;
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_EXTERNALUSER_DETAIL_URL,
                    params);
            log.info("query external user detail response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            if (StringUtils.isNotBlank(response)) {
                QueryExternalUserDetailResponse queryExternalUserDetailResponse =
                        JsonUtils.toObject(response, QueryExternalUserDetailResponse.class);
                rpcResponse.setCode(queryExternalUserDetailResponse.getErrcode());
                rpcResponse.setMessage(queryExternalUserDetailResponse.getErrmsg());
                List<WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUser> followUserList =
                        new ArrayList<>();
                if (!CollectionUtils.isEmpty(queryExternalUserDetailResponse.getFollowUser())) {

                    for (QueryExternalUserDetailResponse.ExternalUserFollowUser item :
                            queryExternalUserDetailResponse.getFollowUser()) {
                        WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUser followUser =
                                new WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUser();
                        BeanUtils.copyProperties(item, followUser);
                        List<WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUserTag> tagList =
                                new ArrayList<>();
                        if (!CollectionUtils.isEmpty(item.getTags())) {
                            for (QueryExternalUserDetailResponse.ExternalUserFollowUserTag itemTag : item.getTags()) {
                                WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUserTag tag =
                                        new WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUserTag();
                                BeanUtils.copyProperties(itemTag, tag);
                                tagList.add(tag);
                            }
                        }
                        followUser.setTags(tagList);
                        followUserList.add(followUser);
                    }
                }
                WeComQueryExternalUserDetailClientResponse.ExternalUserContact contact =
                        new WeComQueryExternalUserDetailClientResponse.ExternalUserContact();

                BeanUtils.copyProperties(queryExternalUserDetailResponse.getExternalContact(), contact);

                clientResponse.setExternalContact(contact);
                clientResponse.setFollowUser(followUserList);
                clientResponse.setNextCursor(queryExternalUserDetailResponse.getNextCursor());

            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query external user detail. corpId={}, agentId={},externalUserId={}", corpId, agentId,
                    externalUserId
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query external user detail rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse editExternalUserCorpTag(String corpId, String agentId, EditExternalUserCorpTagRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("mark external user corp tag. headerParams={}, requestBody={}", JSON.toJSONString(params),
                    requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.EDIT_EXTERNAL_USER_CORP_TAGS_URL,
                    params,
                    requestBody);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to mark external user corp tag is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("mark external user corp tag response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    WeComBaseResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }

        } catch (Exception e) {
            log.error("failed to mark external user corp tag. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return mark external user corp tag rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

}
