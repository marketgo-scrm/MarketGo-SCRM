package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 17:10
 * Describe:
 */
@Data
public class QueryMomentMassTaskMemberResultResponse extends WeComBaseResponse {
    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("task_list")
    private List<TaskListMessage> taskList;


    @Data
    public static class TaskListMessage {
        @JsonProperty("userid")
        private String userId;
        @JsonProperty("publish_status")
        private int publishStatus;
    }
}
