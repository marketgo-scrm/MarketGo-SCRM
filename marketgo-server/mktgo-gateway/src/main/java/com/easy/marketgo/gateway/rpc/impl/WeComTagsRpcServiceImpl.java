package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.tag.WeComAddCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComDeleteCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComEditCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComQueryTagsClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComTagsRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.tag.AddCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.DeleteCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.EditCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.request.tag.QueryCorpTagsRequest;
import com.easy.marketgo.gateway.wecom.sevice.CorpTagsManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/22/22 2:44 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComTagsRpcServiceImpl implements WeComTagsRpcService {

    @Autowired
    private CorpTagsManagerService corpTagsManagerService;

    @Override
    public RpcResponse addCorpTags(WeComAddCorpTagsClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        AddCorpTagsRequest addCorpTagsRequest = new AddCorpTagsRequest();
        BeanUtils.copyProperties(request, addCorpTagsRequest);
        List<AddCorpTagsRequest.TagMessage> tags = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getTag())) {
            request.getTag().forEach(tag -> {
                AddCorpTagsRequest.TagMessage message = new AddCorpTagsRequest.TagMessage();
                BeanUtils.copyProperties(tag, message);
                tags.add(message);
            });
        }
        addCorpTagsRequest.setTag(tags);
        return corpTagsManagerService.addCorpTags(request.getCorpId(), request.getAgentId(), addCorpTagsRequest);
    }

    @Override
    public RpcResponse queryCorpTags(WeComQueryTagsClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        QueryCorpTagsRequest queryCorpTagsRequest = new QueryCorpTagsRequest();
        if (CollectionUtils.isNotEmpty(request.getGroupIds())) {
            queryCorpTagsRequest.setGroupIds(request.getGroupIds());
        }

        if (CollectionUtils.isNotEmpty(request.getTagIds())) {
            queryCorpTagsRequest.setTagIds(request.getTagIds());
        }
        return corpTagsManagerService.queryCorpTags(request.getCorpId(), request.getAgentId(), queryCorpTagsRequest);
    }

    @Override
    public RpcResponse editCorpTags(WeComEditCorpTagsClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        EditCorpTagsRequest editCorpTagsRequest = new EditCorpTagsRequest();
        BeanUtils.copyProperties(request, editCorpTagsRequest);

        return corpTagsManagerService.editCorpTags(request.getCorpId(), request.getAgentId(), editCorpTagsRequest);
    }

    @Override
    public RpcResponse deleteCorpTags(WeComDeleteCorpTagsClientRequest request) {
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }

        DeleteCorpTagsRequest deleteCorpTagsRequest = new DeleteCorpTagsRequest();
        if(CollectionUtils.isNotEmpty(request.getGroupId())) {
            deleteCorpTagsRequest.setGroupId(request.getGroupId());
        }
        if(CollectionUtils.isNotEmpty(request.getTagId())) {
            deleteCorpTagsRequest.setTagId(request.getTagId());
        }

        return corpTagsManagerService.deleteCorpTags(request.getCorpId(), request.getAgentId(), deleteCorpTagsRequest);
    }
}
