package com.easy.marketgo.gateway.wecom.sevice;

import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.model.wecom.QueryAgentAccessTokenResponse;
import com.easy.marketgo.core.model.wecom.QueryTokenBaseRequest;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.response.QueryAgentDetailResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 09:59
 * Describe:
 */
@Slf4j
@Service
public class AgentManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    public String getWeComAppSecret(String corpId, String agentId) {

        WeComAgentMessageEntity weComAgentMessageEntity =
                weComAgentMessageRepository.getWeComAgentByCorpAndAgent(corpId, agentId);
        if (weComAgentMessageEntity == null) {
            return null;
        }
        return weComAgentMessageEntity.getSecret();
    }

    public RpcResponse checkAgentParamIsValid(String corpId, String agentId, String secret) {
        if (StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId) || StringUtils.isEmpty(secret)) {
            log.error("check agent params error, params can not be empty. corpId={}, agentId={}, secret={}",
                    corpId, agentId, secret);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        RpcResponse rpcResponse = RpcResponse.success();
        QueryTokenBaseRequest request = new QueryTokenBaseRequest();
        request.setCorpId(corpId);
        request.setAgentId(agentId);
        request.setSecret(secret);

        String accessTokenResponse = accessTokenManagerService.getTokenFromWeCom(request);
        QueryAgentAccessTokenResponse queryAgentAccessTokenResponse =
                JsonUtils.toObject(accessTokenResponse, QueryAgentAccessTokenResponse.class);
        // corpId或者secret错误
        if (!queryAgentAccessTokenResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.warn("check agent params error, corpId or secret error. corpId={}, secret={}, accessTokenResponse={}",
                    corpId, secret, accessTokenResponse);
            rpcResponse.setCode(queryAgentAccessTokenResponse.getErrcode());
            rpcResponse.setMessage(queryAgentAccessTokenResponse.getErrmsg());
            return rpcResponse;
        }
        accessTokenManagerService.setAccessToken(corpId, agentId, queryAgentAccessTokenResponse.getAccessToken(),
                queryAgentAccessTokenResponse.getExpiresIn());
        if (agentId.equalsIgnoreCase(Constants.AGENT_KEY_FOR_CONTACTS) || agentId.equalsIgnoreCase(Constants.AGENT_KEY_FOR_EXTERNALUSER)) {
            return RpcResponse.success();
        }
        return queryAgentDetail(corpId, agentId);
    }

    public RpcResponse queryAgentDetail(String corpId, String agentId) {
        if (StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId)) {
            log.error("fail to get agent detail, params can not be empty. accessToken={}, agentId={}", corpId, agentId);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        RpcResponse rpcResponse = RpcResponse.success();
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> params = Maps.newHashMap();
        params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        params.put(WeComHttpConstants.AGENTID, agentId);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_AGENT_DETAIL_URL, params);
        } catch (Exception e) {
            log.error("failed to query agent message.corpId={}, agentId={}", corpId, agentId, e);
        }
        log.info("check agent detail. corpId={}, agentId={}, response={}", corpId, agentId, response);
        if (StringUtils.isNotBlank(response)) {
            QueryAgentDetailResponse queryAgentDetailResponse = JsonUtils.toObject(response,
                    QueryAgentDetailResponse.class);
            rpcResponse.setCode(queryAgentDetailResponse.getErrcode());
            rpcResponse.setMessage(queryAgentDetailResponse.getErrmsg());
            return rpcResponse;
        }
        return RpcResponse.failure(ErrorCodeEnum.ERROR_WEB_INTERNAL_SERVICE);
    }
}
