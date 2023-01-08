package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:31
 * Describe:
 */
@Data
public class QueryMomentMassTaskCreateResultResponse extends WeComBaseResponse {
    private int status;
    private String type;
    private ResultMessage result;

    @Data
    public static class ResultMessage implements Serializable {
        @JsonProperty("errcode")
        private int code;
        @JsonProperty("errmsg")
        private String message;
        @JsonProperty("moment_id")
        private String momentId;

        @JsonProperty("invalid_sender_list")
        private ResultInvalidSenderList invalidSenderList;

        @JsonProperty("invalid_external_contact_list")
        private ResultInvalidExternalList invalidExternalContactList;
    }


    @Data
    public static class ResultInvalidSenderList {
        @JsonProperty("user_list")
        private List<String> userList;
        @JsonProperty("department_list")
        private List<Integer> departmentList;
    }


    @Data
    public static class ResultInvalidExternalList {
        @JsonProperty("tag_list")
        private List<String> tagList;
    }
}
