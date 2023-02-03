package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.WeComTaskCenterRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/15/22 6:26 PM
 * Describe:
 */
public interface WeComTaskCenterService {

    BaseResponse updateOrInsertTaskCenter(String projectId, String corpId, String taskType,
                                          WeComTaskCenterRequest weComTaskCenterRequest);

    BaseResponse remindSendTask(String projectId, String corpId, String taskType, String taskUuid);

    BaseResponse listTaskCenter(String projectId,
                                List<String> taskTypes,
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
                             String status, String planTime);

    BaseResponse getTaskCenterDetails(String projectUuid, Integer taskId);

    BaseResponse getTaskCenterStatistic(String taskUuid, String metricsType, String planTime);

    BaseResponse getTaskCenterCreators(String projectUuid, String corpId, String taskType);

    BaseResponse deleteTaskCenter(String taskType, String taskUuid);

    BaseResponse checkTaskCenterName(String projectId, String taskType, Integer taskId, String name);
}
