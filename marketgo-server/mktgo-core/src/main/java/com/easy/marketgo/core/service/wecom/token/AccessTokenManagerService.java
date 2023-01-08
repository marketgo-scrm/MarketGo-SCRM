package com.easy.marketgo.core.service.wecom.token;

import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.wecom.WeComConstants;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import com.easy.marketgo.core.model.wecom.QueryAgentAccessTokenResponse;
import com.easy.marketgo.core.model.wecom.QueryTokenBaseRequest;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComCorpMessageRepository;
import com.easy.marketgo.core.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 17:17
 * Describe:
 */
@Slf4j
@Service
public class AccessTokenManagerService extends TokenBaseManagerService {

    @Resource
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComCorpMessageRepository weComCorpMessageRepository;

    public String getAgentAccessToken(final String corpId, final String agentId) {
        String cacheKey = String.format(WeComConstants.CACHE_KEY_AGENT_ACCESS_TOKEN, corpId, agentId);
        String accessToken = getTokenFromCache(cacheKey);
        if (StringUtils.isBlank(accessToken)) {
            try {
                lock();
                accessToken = getTokenFromCache(cacheKey);
                if (!StringUtils.isBlank(accessToken)) {
                    return accessToken;
                }
                String response = getTokenFromWeCom(buildParams(corpId, agentId));
                accessToken = parserTokenResponse(cacheKey, response);
            } catch (Exception e) {
                log.error("failed to get agent access addressToken from weCom. cacheKey={}", cacheKey, e);
            } finally {
                unlock();
            }
        }
        return accessToken;
    }

    public void flushAgentAccessToken(final String corpId, final String agentId) {
        String response = getTokenFromWeCom(buildParams(corpId, agentId));
        String cacheKey = String.format(WeComConstants.CACHE_KEY_AGENT_ACCESS_TOKEN, corpId, agentId);
        parserTokenResponse(cacheKey, response);
    }

    public void setAccessToken(final String corpId, final String agentId, final String accessToken,
                               final long expiresIn) {
        String cacheKey = String.format(WeComConstants.CACHE_KEY_AGENT_ACCESS_TOKEN, corpId, agentId);
        setTokenToCache(cacheKey, accessToken, expiresIn);
    }

    @Override
    public String getTokenFromWeCom(QueryTokenBaseRequest request) {
        HashMap<String, String> params = new HashMap<>();
        params.put(WeComHttpConstants.CORPID, request.getCorpId());
        params.put(WeComHttpConstants.CORPSECRET, request.getSecret());
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.AGENT_ACCESS_TOKEN_URL, params);
        } catch (Exception e) {
            log.error("failed to query agent access addressToken. ", e);
        }


        return response;
    }

    private String getWeComAppSecret(String corpId, String agentId) {
        if (agentId.equals(Constants.AGENT_KEY_FOR_CONTACTS)) {
            WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
            return (weComCorpMessageEntity == null) ? null : weComCorpMessageEntity.getContactsSecret();
        } else if (agentId.equals(Constants.AGENT_KEY_FOR_EXTERNALUSER)) {
            WeComCorpMessageEntity weComCorpMessageEntity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
            return (weComCorpMessageEntity == null) ? null : weComCorpMessageEntity.getExternalUserSecret();
        } else {
            WeComAgentMessageEntity weComAgentMessageEntity =
                    weComAgentMessageRepository.getWeComAgentByCorpAndAgent(corpId, agentId);
            if (weComAgentMessageEntity == null) {
                return null;
            }
            return weComAgentMessageEntity.getSecret();
        }
    }

    private QueryTokenBaseRequest buildParams(String corpId, String agentId) {
        String agentSecret = getWeComAppSecret(corpId, agentId);

        QueryTokenBaseRequest request = new QueryTokenBaseRequest();
        request.setCorpId(corpId);
        request.setAgentId(agentId);
        request.setSecret(agentSecret);

        return request;
    }

    public String parserTokenResponse(String cacheKey, String response) {
        if (StringUtils.isBlank(response)) {
            log.error("failed to query agent access addressToken.");
            return null;
        }
        log.info("query agent access addressToken response from weCom. response={}", StringUtils.isEmpty(response) ?
                "" : response);
        QueryAgentAccessTokenResponse queryAgentAccessTokenResponse =
                JsonUtils.toObject(response, QueryAgentAccessTokenResponse.class);
        if (!queryAgentAccessTokenResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query agent access addressToken. response={}", response);
            setTokenToCache(cacheKey, queryAgentAccessTokenResponse.getAccessToken(),
                    queryAgentAccessTokenResponse.getExpiresIn());
        }
        return queryAgentAccessTokenResponse.getAccessToken();
    }
}
