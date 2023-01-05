package com.easy.marketgo.react.service.client;

import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailClientResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/3/23 11:33 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComClientTaskCenterServiceImpl implements WeComClientTaskCenterService {

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @Override
    public BaseResponse listTaskCenter(List<String> type, List<String> taskTypes, Integer pageNum, Integer pageSize,
                                       String corpId, List<String> statuses, String keyword, String memberId,
                                       List<String> createUserIds, String sortKey, String sortOrder, String startTime
            , String endTime) {
        return null;
    }

    @Override
    public BaseResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid) {
        String value = taskCacheManagerService.getMemberCache(corpId, memberId, taskUuid, uuid);
        if (StringUtils.isBlank(value)) {
            BaseResponse.failure(ErrorCodeEnum.ERROR_REACT_TASK_IS_NOT_EXIST);
        }
        String content = taskCacheManagerService.getCacheContent(taskUuid);
        if (StringUtils.isBlank(content)) {
            BaseResponse.failure(ErrorCodeEnum.ERROR_REACT_TASK_CONTENT_IS_NOT_EXIST);
        }
        BaseResponse response = BaseResponse.success();

        WeComTaskCenterDetailClientResponse detailClientResponse = JsonUtils.toObject(content,
                WeComTaskCenterDetailClientResponse.class);
        log.info("finish to query task center detail. response={}", detailClientResponse);
        response.setData(detailClientResponse);
        return response;
    }

    @Override
    public BaseResponse changeTaskCenterMemberStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                     String status) {
        return null;
    }

    @Override
    public BaseResponse changeTaskCenterExternalUserStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                           String externalUserId, String status) {
        return null;
    }
}
