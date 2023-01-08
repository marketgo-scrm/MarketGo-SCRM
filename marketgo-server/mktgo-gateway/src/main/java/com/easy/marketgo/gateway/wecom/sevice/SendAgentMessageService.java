package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.SendAgentMessageRequest;
import com.easy.marketgo.gateway.wecom.response.SendAgentMessageResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 10:26
 * Describe:
 */
@Service
@Slf4j
public class SendAgentMessageService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse sendAgentMessage(String corpId, String agentId, SendAgentMessageRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("send agent message. headerParams={}, requestBody={}", JSON.toJSONString(params), requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.SEND_AGNET_MESSAGE_URL, params,
                    requestBody);
            if (StringUtils.isBlank(response)) {
                log.error("failed to send agent message is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("send agent message response from weCom. response={}", response);
            SendAgentMessageResponse sendAgentMessageResponse = JsonUtils.toObject(response,
                    SendAgentMessageResponse.class);
            if (sendAgentMessageResponse != null) {
                rpcResponse.setCode(sendAgentMessageResponse.getErrcode());
                rpcResponse.setMessage(sendAgentMessageResponse.getErrmsg());
            }

        } catch (Exception e) {
            log.error("failed to send welcome msg. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return send welcome msg rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

}
