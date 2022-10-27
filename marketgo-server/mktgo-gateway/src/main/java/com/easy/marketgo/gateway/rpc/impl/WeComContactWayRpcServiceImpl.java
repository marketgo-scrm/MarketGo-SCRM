package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.contactway.WeComContactWayClientRequest;
import com.easy.marketgo.api.model.request.contactway.WeComDeleteContactWayClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComContactWayRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.AddOrUpdateContactWayRequest;
import com.easy.marketgo.gateway.wecom.request.DeleteContactWayRequest;
import com.easy.marketgo.gateway.wecom.sevice.ContactWayManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:35 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComContactWayRpcServiceImpl implements WeComContactWayRpcService {

    @Autowired
    private ContactWayManagerService contactWayManagerService;

    @Override
    public RpcResponse addContactWay(WeComContactWayClientRequest request) {
        log.info("receive rpc for add contact way");
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        AddOrUpdateContactWayRequest addOrUpdateContactWayRequest = new AddOrUpdateContactWayRequest();
        BeanUtils.copyProperties(request, addOrUpdateContactWayRequest);
        return contactWayManagerService.addContactWay(request.getCorpId(), request.getAgentId(),
                addOrUpdateContactWayRequest);
    }

    @Override
    public RpcResponse updateContactWay(WeComContactWayClientRequest request) {
        log.info("receive rpc for update contact way");
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        AddOrUpdateContactWayRequest addOrUpdateContactWayRequest = new AddOrUpdateContactWayRequest();
        BeanUtils.copyProperties(request, addOrUpdateContactWayRequest);
        return contactWayManagerService.updateContactWay(request.getCorpId(), request.getAgentId(),
                addOrUpdateContactWayRequest);
    }

    @Override
    public RpcResponse deleteContactWay(WeComDeleteContactWayClientRequest request) {
        log.info("receive rpc for delete contact way");
        if (request == null) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        DeleteContactWayRequest deleteContactWayRequest = new DeleteContactWayRequest();
        BeanUtils.copyProperties(request, deleteContactWayRequest);
        return contactWayManagerService.deleteContactWay(request.getCorpId(), request.getAgentId(),
                deleteContactWayRequest);
    }
}
