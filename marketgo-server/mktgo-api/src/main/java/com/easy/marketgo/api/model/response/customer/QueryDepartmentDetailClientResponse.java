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
public class QueryDepartmentDetailClientResponse implements Serializable {
    private WeComDepartmentMessage department;

    @Data
    public static class WeComDepartmentMessage implements Serializable {
        private Long id;
        private String name;
        private String nameEn;
        private List<String> departmentLeader;
        private Long parentId;
        private Long order;
    }
}
