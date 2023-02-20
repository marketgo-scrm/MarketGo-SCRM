package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.request.WeComMassTaskRequest;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/15/22 6:26 PM
 * Describe:
 */
public interface WeComMassTaskService {

    BaseResponse updateOrInsertMassTask(String projectId, String corpId, String taskType,
                                        WeComMassTaskRequest weComMassTaskRequest);

    BaseResponse remindSendTask(String projectId, String corpId, String taskType, String taskUuid);

    BaseResponse listMassTask(String projectId,
                              String taskType,
                              Integer pageNum,
                              Integer pageSize,
                              String corpId,
                              List<String> statuses,
                              String keyword,
                              List<String> createUserIds,
                              String sortKey,
                              String sortOrder,
                              String startTime,
                              String endTime);

    BaseResponse listMembers(String projectId,
                             String corpId,
                             String taskType,
                             String metricsType,
                             Integer pageNum,
                             Integer pageSize,
                             String taskUuid,
                             String keyword,
                             String status);


    BaseResponse getMassTaskDetails(String projectUuid, Integer taskId);

    BaseResponse getMassTaskStatistic(String taskUuid, String metricsType);

    BaseResponse getMassTaskCreators(String projectUuid, String corpId, String taskType);

    BaseResponse deleteMassTask(String taskType, String taskUuid);

    BaseResponse checkMassTaskName(String projectId, String taskType, Integer taskId, String name);

    BaseResponse stopMassTaskMessage(String projectId, String corpId, String taskType, String taskUuid);
}
