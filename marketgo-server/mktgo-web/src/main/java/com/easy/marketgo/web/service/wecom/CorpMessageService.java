package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.WeComAgentMessageRequest;
import com.easy.marketgo.web.model.request.WeComCorpMessageRequest;
import com.easy.marketgo.web.model.request.WeComForwardServerMessageRequest;
import com.easy.marketgo.web.model.response.BaseResponse;
import com.easy.marketgo.web.model.response.corp.WeComCorpCallbackResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/17/22 4:08 PM
 * Describe:
 */

public interface CorpMessageService {

    BaseResponse checkAgentParams(String projectId, String corpId, String agentId, String secret);

    BaseResponse updateOrInsertCorpMessage(String projectId, WeComCorpMessageRequest weComCorpMessageRequest);

    BaseResponse updateOrInsertAgentMessage(String projectId, WeComAgentMessageRequest agentMessageRequest);

    BaseResponse getCallbackConfig(String projectId, String corpId, String configType);

    BaseResponse getCorpConfig(String projectId);

    void getExternalUserDetail(String corpId, String externalUserId);

    BaseResponse updateOrInsertForwardServer(String projectId, String corpId, WeComForwardServerMessageRequest request);

    BaseResponse getForwardServer(String projectId, String corpId);
}
