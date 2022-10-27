package com.easy.marketgo.api.model.response.masstask;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 7:19 PM
 * Describe:
 */


@Data
public class WeComMomentMassTaskPublishResultClientResponse implements Serializable {
    private String nextCursor;

    private List<TaskListMessage> taskList;


    @Data
    public static class TaskListMessage implements Serializable {
        private String userId;
        private int publishStatus;
    }
}
