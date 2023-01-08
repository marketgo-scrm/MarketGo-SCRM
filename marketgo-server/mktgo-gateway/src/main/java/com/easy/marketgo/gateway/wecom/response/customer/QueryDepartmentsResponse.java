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
public class QueryDepartmentsResponse extends WeComBaseResponse {
    @JsonProperty("department_id")
    private List<WeComDepartmentMessage> department;

    @Data
    public static class WeComDepartmentMessage {
        private Long id;
        @JsonProperty("parentid")
        private Long parentId;
        private Long order;
    }
}
