package com.easy.marketgo.api.model.response.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/5/22 5:46 PM
 * Describe:
 */
@Data
public class QueryMemberDetailClientResponse implements Serializable {
    private String userId;
    private String name;
    private List<Long> department;
    private List<Long> order;
    private Integer status;
}
