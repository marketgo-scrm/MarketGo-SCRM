package com.easy.marketgo.core.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 9/5/22 12:07 PM
 * Describe:
 */
@Data
public class WeComLiveCodeMember {
    private List<DepartmentMessage> departments;
    private List<UserMessage> users;

    @Data
    public static class DepartmentMessage {
        private Long id;
        private String name;
    }

    @Data
    public static class UserMessage {
        private String memberId;
        private String memberName;
    }
}
