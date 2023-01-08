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
public class QueryMembersResponse extends WeComBaseResponse {
    @JsonProperty("dept_user")
    private List<DepartmentMember> deptUser;
    @JsonProperty("next_cursor")
    private String nextCursor = null;
    @Data
    public static class DepartmentMember {
        @JsonProperty("userid")
        private String userId;
        private Long department;
    }
}
