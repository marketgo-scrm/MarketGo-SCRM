package com.easy.marketgo.react.service.client;

import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.crypto.SHA1;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.SdkConfigSignatureTyeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.repository.wecom.WeComCorpMessageRepository;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.react.model.QuerySignatureResponse;
import com.easy.marketgo.react.model.WeComAuth2VerifyResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.easy.marketgo.common.constants.wecom.WeComHttpConstants.NONCESTR;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/3/23 11:33 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComClientVerifyService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    @Autowired
    private ApiTicketManagerService apiTicketManagerService;

    @Resource
    private WeComCorpMessageRepository weComCorpMessageRepository;

    public BaseResponse userVerify(final String corpId, final String agentId, final String code,
                                   final String memberId) {

        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || StringUtils.isBlank(code)) {
            log.error("fail to verify user message, params can not be empty. corpId={}, agentId={}, code={}", corpId,
                    agentId, code);
            return BaseResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        BaseResponse baseResponse = BaseResponse.success();
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> params = Maps.newHashMap();
        params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        params.put(WeComHttpConstants.AUTH_VERIFY_CODE, agentId);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.MEMBERID_GET_FOR_OAUTH2, params);
        } catch (Exception e) {
            log.error("failed to query getMemberIdForOauth2 message.corpId={}, agentId={}", corpId, agentId, e);
        }
        log.info("check getMemberIdForOauth2 detail. corpId={}, agentId={}, response={}", corpId, agentId, response);
        if (StringUtils.isNotBlank(response)) {
            WeComAuth2VerifyResponse weComAuth2VerifyResponse = JsonUtils.toObject(response,
                    WeComAuth2VerifyResponse.class);
            baseResponse.setCode(weComAuth2VerifyResponse.getErrcode());
            baseResponse.setMessage(weComAuth2VerifyResponse.getErrmsg());
        }
        return baseResponse;
    }

    /**
     * 签名字符串验证
     * 参考：https://work.weixin.qq.com/api/doc/90000/90136/90506
     *
     * @param corpId
     * @param agentId
     * @param type
     * @param url
     * @return
     */

    public BaseResponse sdkConfigVerify(final String corpId, final String agentId, final String type,
                                        final String url) {


        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || StringUtils.isBlank(type) ||
                StringUtils.isBlank(url)) {
            log.error("fail to verify sdk config message, params can not be empty. corpId={}, agentId={}, code={}",
                    corpId, agentId, type);
            return BaseResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        long timestamp = System.currentTimeMillis() / 1000;
        String agentSignature = null;
        String corpSignature = null;

        if (SdkConfigSignatureTyeEnum.ALL.getValue().equals(type)) {
            agentSignature =
                    generateSignature(apiTicketManagerService.getAgentTicket(corpId, agentId), NONCESTR, timestamp,
                            dealURL(url));
            corpSignature =
                    generateSignature(apiTicketManagerService.getCorpTicket(corpId, agentId),
                            NONCESTR, timestamp,
                            dealURL(url));
        } else if (SdkConfigSignatureTyeEnum.CORP.getValue().equals(type)) {
            corpSignature =
                    generateSignature(apiTicketManagerService.getCorpTicket(corpId, agentId),
                            NONCESTR, timestamp,
                            dealURL(url));
        } else if (SdkConfigSignatureTyeEnum.AGENT.getValue().equals(type)) {
            agentSignature =
                    generateSignature(apiTicketManagerService.getAgentTicket(corpId, agentId),
                            NONCESTR, timestamp,
                            dealURL(url));
        }

        BaseResponse baseResponse = BaseResponse.success();

        QuerySignatureResponse querySignatureResponse = new QuerySignatureResponse();
        if (StringUtils.isNotBlank(corpSignature)) {
            QuerySignatureResponse.CorpSignatureMessage corp = new QuerySignatureResponse.CorpSignatureMessage();
            corp.setSignature(corpSignature);
            corp.setTimestamp(timestamp);
            corp.setNonceStr(NONCESTR);
            querySignatureResponse.setCorpSignature(corp);

        }
        if (StringUtils.isNotBlank(agentSignature)) {
            QuerySignatureResponse.AgentSignatureMessage agent = new QuerySignatureResponse.AgentSignatureMessage();
            agent.setSignature(agentSignature);
            agent.setTimestamp(timestamp);
            agent.setNonceStr(NONCESTR);
            querySignatureResponse.setAgentSignature(agent);
        }
        baseResponse.setData(querySignatureResponse);
        return baseResponse;
    }

    /**
     * 生成签名字符串
     * 参考：https://work.weixin.qq.com/api/doc/90000/90136/90506
     *
     * @param jsapiTicket
     * @param nonceStr
     * @param timestamp
     * @param url
     * @return
     */
    private String generateSignature(String jsapiTicket, String nonceStr, long timestamp, String url) {
        String str =
                SHA1.gen(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s", jsapiTicket, nonceStr,
                        timestamp, url));
        return str;
    }

    /**
     * 除去#后面的部分
     *
     * @param url
     * @return
     */
    private String dealURL(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        int index = url.indexOf('#');
        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }

    public BaseResponse checkCredFile(String fileName, HttpServletResponse httpServletResponse) {
        WeComCorpMessageEntity entity = weComCorpMessageRepository.getCorpConfigByCredFileName(fileName);
        if (entity == null) {
            return null;
        }
        String content = entity.getCredFileContent();
        log.info("weCom file verify content. content={}", content);
        httpServletResponse.setHeader("Content-Type", "application/octet-stream");
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
                fileName));
        OutputStream outputStream = null;

        try {
            outputStream = httpServletResponse.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
