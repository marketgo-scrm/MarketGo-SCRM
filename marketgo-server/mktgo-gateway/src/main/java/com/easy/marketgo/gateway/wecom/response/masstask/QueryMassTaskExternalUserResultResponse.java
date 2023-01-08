package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:29
 * Describe:
 */
@Data
public class QueryMassTaskExternalUserResultResponse extends WeComBaseResponse {
    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("send_list")
    private List<SendListMessage> sendList;


    @Data
    public static class SendListMessage implements Serializable {
        @JsonProperty("userid")
        private String userId;
        @JsonProperty("external_userid")
        private String externalUserId;
        @JsonProperty("chat_id")
        private String chatId;
        private int status;
        @JsonProperty("send_time")
        private int sendTime;
    }
}
