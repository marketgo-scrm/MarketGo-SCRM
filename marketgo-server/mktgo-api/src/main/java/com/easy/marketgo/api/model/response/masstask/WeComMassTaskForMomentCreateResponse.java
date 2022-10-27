package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:05 PM
 * Describe:
 */


@Data
public class WeComMassTaskForMomentCreateResponse implements Serializable {
    private Integer status;
    private String type;
    private ResultMessage result;

    @Data
    public static class ResultMessage implements Serializable {
        private Integer code;
        private String message;
        private String momentId;

        private ResultInvalidSenderList invalidSenderList;
        private ResultInvalidExternalList invalidExternalContactList;
    }

    @Data
    public static class ResultInvalidSenderList implements Serializable {
        private List<String> userList;
        private List<Integer> departmentList;
    }

    @Data
    public static class ResultInvalidExternalList implements Serializable {
        private List<String> tagList;
    }
}
