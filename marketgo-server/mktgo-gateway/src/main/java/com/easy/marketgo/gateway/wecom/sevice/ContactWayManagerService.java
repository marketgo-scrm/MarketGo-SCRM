package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComAddContactWayClientResponse;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.AddOrUpdateContactWayRequest;
import com.easy.marketgo.gateway.wecom.request.DeleteContactWayRequest;
import com.easy.marketgo.gateway.wecom.response.AddContactWayResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:37 PM
 * Describe:
 */
@Service
@Slf4j
public class ContactWayManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse addContactWay(String corpId, String agentId, AddOrUpdateContactWayRequest request) {

        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("get qrCode for add contact way. headerParams={}, requestBody={}", JSON.toJSONString(params),
                    requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.ADD_CONTACT_WAY_URL, params,
                    requestBody);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to get qrCode for add contact way is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("get qrCode for add contact way response from weCom. response={}", response);
            AddContactWayResponse addContactWayResponse = JsonUtils.toObject(response,
                    AddContactWayResponse.class);
            if (addContactWayResponse != null) {
                rpcResponse.setCode(addContactWayResponse.getErrcode());
                rpcResponse.setMessage(addContactWayResponse.getErrmsg());
                WeComAddContactWayClientResponse weComAddContactWayClientResponse =
                        new WeComAddContactWayClientResponse();
                weComAddContactWayClientResponse.setConfigId(addContactWayResponse.getConfigId());
                weComAddContactWayClientResponse.setQrCode(addContactWayResponse.getQrCode());
                rpcResponse.setData(weComAddContactWayClientResponse);
            }

        } catch (Exception e) {
            log.error("failed to get qrCode for add contact way. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return get qrCode for add contact way rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse updateContactWay(String corpId, String agentId, AddOrUpdateContactWayRequest request) {

        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("update contact way. headerParams={}, requestBody={}", JSON.toJSONString(params), requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.UPDATE_CONTACT_WAY_URL, params,
                    requestBody);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to update contact way is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("update contact way response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    WeComBaseResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }

        } catch (Exception e) {
            log.error("failed to update contact way. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return update contact way rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    public RpcResponse deleteContactWay(String corpId, String agentId, DeleteContactWayRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("delete contact way. headerParams={}, requestBody={}", JSON.toJSONString(params), requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.DELETE_CONTACT_WAY_URL, params,
                    requestBody);
            if (!StringUtils.isNotBlank(response)) {
                log.error("failed to delete contact way is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("delete contact way response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    WeComBaseResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }

        } catch (Exception e) {
            log.error("failed to delete contact way. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return delete contact way rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
