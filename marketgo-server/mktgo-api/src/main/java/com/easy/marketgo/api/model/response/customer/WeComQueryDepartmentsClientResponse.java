package com.easy.marketgo.api.model.response.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 10:54 PM
 * Describe:
 */
@Data
public class WeComQueryDepartmentsClientResponse implements Serializable {

    private static final long serialVersionUID = 2306486305896806006L;

    private List<WeComDepartmentMessage> department;

    @Data
    public static class WeComDepartmentMessage implements Serializable {
        private Long id;
        private Long parentId;
        private Long order;
    }
}
