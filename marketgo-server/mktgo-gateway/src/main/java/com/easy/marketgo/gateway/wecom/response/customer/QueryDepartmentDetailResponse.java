package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/5/22 5:46 PM
 * Describe:
 */
@Data
public class QueryDepartmentDetailResponse extends WeComBaseResponse {
    private WeComDepartmentMessage department;

    @Data
    public static class WeComDepartmentMessage {
        private Long id;
        private String name;
        @JsonProperty("name_en")
        private String nameEn;
        @JsonProperty("department_leader")
        private List<String> departmentLeader;
        @JsonProperty("parentid")
        private Long parentId;
        private Long order;
    }
}
