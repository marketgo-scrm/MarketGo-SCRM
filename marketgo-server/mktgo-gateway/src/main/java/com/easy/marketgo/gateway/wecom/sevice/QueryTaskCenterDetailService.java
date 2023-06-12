package com.easy.marketgo.gateway.wecom.sevice;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComChangeStatusRequest;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/6/23 1:29 PM
 * Describe:
 */

public interface QueryTaskCenterDetailService {

    BaseResponse listTaskCenter(String corpId,
                                String memberId,
                                List<String> taskTypes,
                                List<String> statuses,
                                String startTime,
                                String endTime,
                                Integer pageNum,
                                Integer pageSize);

    BaseResponse listSubTaskCenter(String corpId,
                                String memberId,
                                String taskUuid,
                                Integer pageNum,
                                Integer pageSize);

    BaseResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid);

    BaseResponse changeTaskCenterMemberStatus(String corpId, WeComChangeStatusRequest request);

    BaseResponse getTaskCenterContent(String corpId, String memberId, String taskUuid);
}
