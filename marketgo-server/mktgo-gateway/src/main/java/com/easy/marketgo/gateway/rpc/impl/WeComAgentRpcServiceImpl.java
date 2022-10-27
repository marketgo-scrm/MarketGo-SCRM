package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.request.WeComCheckAgentMessageRequest;
import com.easy.marketgo.api.service.WeComAgentRpcService;
import com.easy.marketgo.gateway.wecom.sevice.AgentManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 21:03
 * Describe:
 */
@Slf4j
@DubboService
public class WeComAgentRpcServiceImpl implements WeComAgentRpcService {

    @Autowired
    private AgentManagerService agentManagerService;

    @Override
    public RpcResponse checkAgentParams(WeComCheckAgentMessageRequest request) {
        log.info("receive rpc for check agent params");
        return agentManagerService.checkAgentParamIsValid(request.getCorpId(), request.getAgentId(), request.getSecret());
    }
}
