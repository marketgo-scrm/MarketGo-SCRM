package com.easy.marketgo.gateway.wecom.response.masstask;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:27
 * Describe:
 */
@Data
public class QueryMassTaskMemberResultResponse extends WeComBaseResponse {
    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("task_list")
    private List<TaskListMessage> taskList;


    @Data
    public static class TaskListMessage implements Serializable {
        @JsonProperty("userid")
        private String userId;
        private int status;
        @JsonProperty("send_time")
        private int sendTime;
    }
}
