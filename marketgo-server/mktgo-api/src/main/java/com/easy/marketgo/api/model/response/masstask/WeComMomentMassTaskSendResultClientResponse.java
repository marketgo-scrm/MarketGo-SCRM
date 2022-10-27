package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:19 PM
 * Describe:
 */


@Data
public class WeComMomentMassTaskSendResultClientResponse implements Serializable {
    private String nextCursor;

    private List<CustomerListMessage> customerList;


    @Data
    public static class CustomerListMessage implements Serializable {
        private String externalUserId;
    }
}
