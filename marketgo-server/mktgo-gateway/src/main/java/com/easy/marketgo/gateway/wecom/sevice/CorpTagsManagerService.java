package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.tag.WeComAddCorpTagsClientResponse;
import com.easy.marketgo.api.model.response.tag.WeComQueryCorpTagsClientResponse;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.tag.AddCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.DeleteCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.EditCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.QueryCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.response.tag.AddCorpTagsResponse;
import com.easy.marketgo.gateway.wecom.response.tag.QueryCorpTagsResponse;
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
 * @data : 7/22/22 2:55 PM
 * Describe:
 */
@Slf4j
@Service
public class CorpTagsManagerService {
    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse addCorpTags(String corpId, String agentId, AddCorpTagsRequest request) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to add corp tags, params can not be empty. corpId={}, agentId={},request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        WeComAddCorpTagsClientResponse clientResponse = new WeComAddCorpTagsClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("add corp tags.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.ADD_CORP_TAG_LIST_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to add corp tag list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("add corp tag list response from weCom. response={}", response);
            AddCorpTagsResponse addCorpTagsResponse = JsonUtils.toObject(response, AddCorpTagsResponse.class);

            AddCorpTagsResponse.TagGroup tagGroups = addCorpTagsResponse.getTagGroup();
            rpcResponse.setCode(addCorpTagsResponse.getErrcode());
            rpcResponse.setMessage(addCorpTagsResponse.getErrmsg());
            if (tagGroups != null) {
                WeComAddCorpTagsClientResponse.TagGroup message =
                            new WeComAddCorpTagsClientResponse.TagGroup();
                    BeanUtils.copyProperties(tagGroups, message);
                    List<WeComAddCorpTagsClientResponse.TagMessage> tagMessageList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(tagGroups.getTag())) {
                        tagGroups.getTag().forEach(tag -> {
                            WeComAddCorpTagsClientResponse.TagMessage tagMessage =
                                    new WeComAddCorpTagsClientResponse.TagMessage();
                            BeanUtils.copyProperties(tag, tagMessage);
                            tagMessageList.add(tagMessage);
                        });
                    }
                    message.setTag(tagMessageList);
                clientResponse.setTagGroup(message);
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to add corp tag  list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return add corp tag list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse editCorpTags(String corpId, String agentId, EditCorpTagsRequest request) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to edit corp tags, params can not be empty. corpId={}, agentId={},request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        RpcResponse rpcResponse = RpcResponse.success();

        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("edit corp tags.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.EDIT_CORP_TAG_LIST_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to edit corp tag list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("edit corp tag list response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response, WeComBaseResponse.class);

            rpcResponse.setCode(weComBaseResponse.getErrcode());
            rpcResponse.setMessage(weComBaseResponse.getErrmsg());
        } catch (Exception e) {
            log.error("failed to edit corp tag  list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return edit corp tag list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse deleteCorpTags(String corpId, String agentId, DeleteCorpTagsRequest request) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to delete corp tags, params can not be empty. corpId={}, agentId={},request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        RpcResponse rpcResponse = RpcResponse.success();

        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("delete corp tags.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.DELETE_CORP_TAG_LIST_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to delete corp tag list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("delete corp tag list response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response, WeComBaseResponse.class);

            rpcResponse.setCode(weComBaseResponse.getErrcode());
            rpcResponse.setMessage(weComBaseResponse.getErrmsg());
        } catch (Exception e) {
            log.error("failed to delete corp tag  list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return delete corp tag list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse queryCorpTags(String corpId, String agentId, QueryCorpTagsRequest request) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error(
                    "fail to query corp tag list, params can not be empty. corpId={}, agentId={},request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        WeComQueryCorpTagsClientResponse clientResponse = new WeComQueryCorpTagsClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);

            log.info("query corp tag list.headerParams={}, body={}", JSON.toJSONString(params),
                    JsonUtils.toJSONString(request));
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_CORP_TAG_LIST_URL, params,
                    JsonUtils.toJSONString(request));
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to query corp tag list is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
            }
            log.info("query corp tag list response from weCom. response={}", StringUtils.isBlank(response) ? "" :
                    response);
            QueryCorpTagsResponse queryCorpTagsResponse = JsonUtils.toObject(response, QueryCorpTagsResponse.class);

            List<QueryCorpTagsResponse.TagGroup> tagGroups = queryCorpTagsResponse.getTagGroup();
            rpcResponse.setCode(queryCorpTagsResponse.getErrcode());
            rpcResponse.setMessage(queryCorpTagsResponse.getErrmsg());
            if (CollectionUtils.isNotEmpty(tagGroups)) {
                ArrayList<WeComQueryCorpTagsClientResponse.TagGroup> tagGroupList = Lists.newArrayList();
                for (QueryCorpTagsResponse.TagGroup item : tagGroups) {
                    WeComQueryCorpTagsClientResponse.TagGroup message =
                            new WeComQueryCorpTagsClientResponse.TagGroup();
                    BeanUtils.copyProperties(item, message);
                    List<WeComQueryCorpTagsClientResponse.TagMessage> tagMessageList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(item.getTag())) {
                        item.getTag().forEach(tag -> {
                            WeComQueryCorpTagsClientResponse.TagMessage tagMessage =
                                    new WeComQueryCorpTagsClientResponse.TagMessage();
                            BeanUtils.copyProperties(tag, tagMessage);
                            tagMessageList.add(tagMessage);
                        });
                    }
                    message.setTag(tagMessageList);
                    tagGroupList.add(message);
                }
                clientResponse.setTagGroup(tagGroupList);
            }
            rpcResponse.setData(clientResponse);
        } catch (Exception e) {
            log.error("failed to query corp tag  list. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
        }
        log.info("return query corp tag list rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
