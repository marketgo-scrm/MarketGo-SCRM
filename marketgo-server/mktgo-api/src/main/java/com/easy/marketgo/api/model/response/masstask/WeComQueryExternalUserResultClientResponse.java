package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:34 PM
 * Describe:
 */
@Data
public class WeComQueryExternalUserResultClientResponse implements Serializable {
    private String nextCursor;
    private List<SendListMessage> sendList;


    @Data
    public static class SendListMessage implements Serializable {
        private String userId;
        private String externalUserId;
        private String chatId;
        private int status;
        private int sendTime;
    }
}
