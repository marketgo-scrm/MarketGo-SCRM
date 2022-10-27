package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 12:48 PM
 * Describe:
 */
@Data
public class WeComQueryMemberResultClientResponse implements Serializable {
    private String nextCursor;
    private List<TaskListMessage> taskList;

    @Data
    public static class TaskListMessage implements Serializable {
        private String userId;
        private String externalUserId;
        private int status;
        private int sendTime;
    }
}
