package com.easy.marketgo.react.model;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 10:19 AM
 * Describe:
 */
@Data
public class QuerySignatureResponse extends WeComBaseResponse {

    private CorpSignatureMessage corpSignature;

    private AgentSignatureMessage agentSignature;

    @Data
    public static class CorpSignatureMessage {
        @JsonProperty("timestamp")
        private Long timestamp = null;

        @JsonProperty("signature")
        private String signature = null;

        private String nonceStr;
    }

    @Data
    public static class AgentSignatureMessage {
        @JsonProperty("timestamp")
        private Long timestamp = null;

        @JsonProperty("signature")
        private String signature = null;

        private String nonceStr;
    }
}
