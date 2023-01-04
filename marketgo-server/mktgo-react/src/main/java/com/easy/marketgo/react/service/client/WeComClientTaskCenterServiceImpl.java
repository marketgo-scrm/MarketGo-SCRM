package com.easy.marketgo.react.service.client;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public BaseResponse listTaskCenter(List<String> type, List<String> taskTypes, Integer pageNum, Integer pageSize,
                                       String corpId, List<String> statuses, String keyword,String memberId,
                                       List<String> createUserIds, String sortKey, String sortOrder, String startTime
            , String endTime) {
        return null;
    }

    @Override
    public BaseResponse getTaskCenterDetails(String memberId, String taskUuid, String uuid) {
        return null;
    }

    @Override
    public BaseResponse changeTaskCenterMemberStatus(String memberId, String taskUuid, String uuid, String status) {
        return null;
    }

    @Override
    public BaseResponse changeTaskCenterExternalUserStatus(String memberId, String taskUuid, String uuid,
                                                           String externalUserId, String status) {
        return null;
    }
}
