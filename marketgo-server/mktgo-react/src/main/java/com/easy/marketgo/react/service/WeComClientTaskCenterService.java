package com.easy.marketgo.react.service;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailResponse;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 11:51 AM
 * Describe:
 */
public interface WeComClientTaskCenterService {

    BaseResponse listTaskCenter(String corpId,
                                String memberId,
                                List<String> taskTypes,
                                List<String> statuses,
                                String startTime,
                                String endTime,
                                Integer pageNum,
                                Integer pageSize);

    WeComTaskCenterDetailResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid);

    WeComTaskCenterDetailResponse getTaskCenterContent(String corpId, String memberId, String taskUuid);

    BaseResponse changeTaskCenterMemberStatus(String corpId, String memberId, String taskUuid, String uuid,
                                              String sentTime, String status);

    BaseResponse changeTaskCenterExternalUserStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                    String externalUserId, String sentTime, String status);
}
