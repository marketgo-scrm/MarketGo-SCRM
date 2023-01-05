package com.easy.marketgo.react.service;

import com.easy.marketgo.core.model.bo.BaseResponse;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 11:51 AM
 * Describe:
 */
public interface WeComClientTaskCenterService {

    BaseResponse listTaskCenter(List<String> type,
                                List<String> taskTypes,
                                Integer pageNum,
                                Integer pageSize,
                                String corpId,
                                List<String> statuses,
                                String keyword,
                                String memberId,
                                List<String> createUserIds,
                                String sortKey,
                                String sortOrder,
                                String startTime,
                                String endTime);

    BaseResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid);

    BaseResponse changeTaskCenterMemberStatus(String corpId, String memberId, String taskUuid, String uuid, String status);

    BaseResponse changeTaskCenterExternalUserStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                    String externalUserId, String status);
}
