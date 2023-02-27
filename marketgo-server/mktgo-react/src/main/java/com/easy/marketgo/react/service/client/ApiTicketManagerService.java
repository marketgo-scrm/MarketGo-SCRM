package com.easy.marketgo.react.service.client;

import com.easy.marketgo.common.constants.wecom.WeComConstants;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.SdkConfigSignatureTyeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.wecom.QueryApiTicketRequest;
import com.easy.marketgo.core.model.wecom.QueryTokenBaseRequest;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.service.wecom.token.TokenBaseManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.react.model.WeComApiTicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.easy.marketgo.common.constants.wecom.WeComHttpConstants.WECOM_AGENT_TICKET_URL;
import static com.easy.marketgo.common.constants.wecom.WeComHttpConstants.WECOM_CORP_TICKET_URL;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 5:04 PM
 * Describe:
 */
@Slf4j
@Service
public class ApiTicketManagerService extends TokenBaseManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    @Override
    protected String getTokenFromWeCom(QueryTokenBaseRequest request) {
        Map<String, String> params = new HashMap<>();
        QueryApiTicketRequest param = (QueryApiTicketRequest) request;
        String accessToken = accessTokenManagerService.getAgentAccessToken(request.getCorpId(), request.getAgentId());
        params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String url = (param.getType() == SdkConfigSignatureTyeEnum.CORP) ? WECOM_CORP_TICKET_URL :
                WECOM_AGENT_TICKET_URL;
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getDataSync(url, params);

        } catch (Exception e) {
            log.error("failed to query api ticket. ", e);
        }

        return response;
    }

    /**
     * 获取ticket信息
     *
     * @param corpId  企业ID
     * @param agentId 应用ID
     * @return 返回ticket信息，否则返回""
     */
    public String getCorpTicket(String corpId, String agentId) {
        String cacheKey = String.format(WeComConstants.CACHE_KEY_CORP_API_TICKET, corpId);
        String apiTicket = getTokenFromCache(cacheKey);
        if (StringUtils.isBlank(apiTicket)) {
            try {
                lock();
                apiTicket = getTokenFromCache(cacheKey);
                if (!StringUtils.isBlank(apiTicket)) {
                    return apiTicket;
                }
                QueryApiTicketRequest request = new QueryApiTicketRequest();
                request.setAgentId(agentId);
                request.setCorpId(corpId);
                request.setType(SdkConfigSignatureTyeEnum.CORP);
                String response = getTokenFromWeCom(request);
                apiTicket = parserTicketResponse(cacheKey, response);
            } catch (Exception e) {
                log.error("failed to corp api ticket from weCom. cacheKey={}", cacheKey, e);
            } finally {
                unlock();
            }
        }
        return apiTicket;
    }

    public String getAgentTicket(String corpId, String agentId) {
        String cacheKey = String.format(WeComConstants.CACHE_KEY_AGENT_API_TICKET, corpId, agentId);
        String apiTicket = getTokenFromCache(cacheKey);
        if (StringUtils.isBlank(apiTicket)) {
            try {
                lock();
                apiTicket = getTokenFromCache(cacheKey);
                if (!StringUtils.isBlank(apiTicket)) {
                    return apiTicket;
                }
                QueryApiTicketRequest request = new QueryApiTicketRequest();
                request.setAgentId(agentId);
                request.setCorpId(corpId);
                request.setType(SdkConfigSignatureTyeEnum.AGENT);
                String response = getTokenFromWeCom(request);
                apiTicket = parserTicketResponse(cacheKey, response);
            } catch (Exception e) {
                log.error("failed to get agent api ticket from weCom. cacheKey={}", cacheKey, e);
            } finally {
                unlock();
            }
        }
        return apiTicket;
    }

    private String parserTicketResponse(String cacheKey, String response) {
        if (StringUtils.isBlank(response)) {
            log.error("failed to query api ticket.");
            return null;
        }
        log.info("query api ticket response from weCom. response={}", StringUtils.isEmpty(response) ?
                "" : response);
        WeComApiTicketResponse weComApiTicketResponse =
                JsonUtils.toObject(response, WeComApiTicketResponse.class);
        if (!weComApiTicketResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query agent access addressToken. response={}", response);
            setTokenToCache(cacheKey, weComApiTicketResponse.getTicket(),
                    weComApiTicketResponse.getExpiresIn());
        }
        return weComApiTicketResponse.getTicket();
    }
}
