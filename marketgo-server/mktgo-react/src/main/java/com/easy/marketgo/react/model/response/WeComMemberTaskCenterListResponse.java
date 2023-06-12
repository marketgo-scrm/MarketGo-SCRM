package com.easy.marketgo.react.model.response;

import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:50
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeComMemberTaskCenterListResponse {

    private Integer totalCount;

    private List<MemberTaskCenterDetail> list;

    @Data
    public static class MemberTaskCenterDetail {
        private Integer id;
        private String uuid;
        private String corpId;
        private String memberId;
        private String taskUuid;
        private String name;
        private String taskType;
        private String scheduleType;
        private String planTime;
        private String taskStatus;
    }
}
