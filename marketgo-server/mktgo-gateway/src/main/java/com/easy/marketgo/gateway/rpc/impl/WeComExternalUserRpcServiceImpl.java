package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.tag.WeComEditExternalUserCorpTagClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUserDetailClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUsersForMemberClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComExternalUserRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.EditExternalUserCorpTagRequest;
import com.easy.marketgo.gateway.wecom.sevice.ExternalUserManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 4:44 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComExternalUserRpcServiceImpl implements WeComExternalUserRpcService {

    @Autowired
    private ExternalUserManagerService externalUserManagerService;

    @Override
    public RpcResponse queryExternalUsersForMember(WeComQueryExternalUsersForMemberClientRequest request) {
        log.info("receive rpc for query external user for member");
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        return externalUserManagerService.getExternalUsersForMember(request.getCorpId(), request.getAgentId(),
                request.getMemberId());
    }

    @Override
    public RpcResponse queryExternalUserDetail(WeComQueryExternalUserDetailClientRequest request) {
        log.info("receive rpc for query external user detail");
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        return externalUserManagerService.getExternalUserDetail(request.getCorpId(), request.getAgentId(),
                request.getExternalUserId(), request.getNextCursor());
    }

    @Override
    public RpcResponse editExternalUserCorpTag(WeComEditExternalUserCorpTagClientRequest request) {
        log.info("receive rpc for mark corp tags external user for member. request={}", request);
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        EditExternalUserCorpTagRequest editExternalUserCorpTagRequest = new EditExternalUserCorpTagRequest();
        BeanUtils.copyProperties(request, editExternalUserCorpTagRequest);
        if (CollectionUtils.isNotEmpty(request.getAddTag())) {
            editExternalUserCorpTagRequest.setAddTag(request.getAddTag());
        }
        if (CollectionUtils.isNotEmpty(request.getRemoveTag())) {
            editExternalUserCorpTagRequest.setRemoveTag(request.getRemoveTag());
        }
        return externalUserManagerService.editExternalUserCorpTag(request.getCorpId(), request.getAgentId(),
                editExternalUserCorpTagRequest);
    }
}
