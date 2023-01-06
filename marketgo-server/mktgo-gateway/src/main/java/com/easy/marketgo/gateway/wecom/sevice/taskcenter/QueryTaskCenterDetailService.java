package com.easy.marketgo.gateway.wecom.sevice.taskcenter;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.request.client.WeComTaskCenterDetailClientResponse;
import com.easy.marketgo.react.model.response.WeComTaskCenterDetailResponse;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/6/23 1:29 PM
 * Describe:
 */

@Slf4j
@Service
public class QueryTaskCenterDetailService {

    @Autowired
    private WeComClientTaskCenterService weComClientTaskCenterService;

    public BaseResponse listTaskCenter(List<String> type,
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
                                       String endTime) {
        return null;
    }

    public BaseResponse getTaskCenterDetails(String corpId, String memberId, String taskUuid, String uuid) {
        WeComTaskCenterDetailResponse response = weComClientTaskCenterService.getTaskCenterDetails(corpId, memberId,
                taskUuid, uuid);
        WeComTaskCenterDetailClientResponse clientResponse = new WeComTaskCenterDetailClientResponse();
        BeanUtils.copyProperties(response, clientResponse);
        List<WeComTaskCenterDetailClientResponse.AttachmentsMessage> attachmentsMessageList = new ArrayList<>();
        for (WeComTaskCenterDetailResponse.AttachmentsMessage item : response.getAttachments()) {
            WeComTaskCenterDetailClientResponse.AttachmentsMessage message =
                    new WeComTaskCenterDetailClientResponse.AttachmentsMessage();
            BeanUtils.copyProperties(item, message);
            attachmentsMessageList.add(message);
        }
        clientResponse.setAttachments(attachmentsMessageList);
        List<WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage> taskList = new ArrayList<>();
        WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage taskMessage =
                new WeComTaskCenterDetailClientResponse.WeComTaskCenterMessage();
        taskMessage.setPlanTime(response.getPlanTime());
        taskMessage.setTargetTime(response.getTargetTime());
        taskMessage.setTargetType(response.getTargetType());
        taskMessage.setUuid(response.getUuid());
        List<WeComTaskCenterDetailClientResponse.ExternalUserMessage> externalUserMessageList = new ArrayList<>();
        for (WeComTaskCenterDetailResponse.ExternalUserMessage item : response.getExternalUserId()) {
            WeComTaskCenterDetailClientResponse.ExternalUserMessage user =
                    new WeComTaskCenterDetailClientResponse.ExternalUserMessage();
            BeanUtils.copyProperties(item, user);
            externalUserMessageList.add(user);
        }
        taskMessage.setExternalUserId(externalUserMessageList);
        taskList.add(taskMessage);
        clientResponse.setTaskList(taskList);
        return BaseResponse.success(clientResponse);
    }

    public BaseResponse changeTaskCenterMemberStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                     String status) {
        return null;
    }

    public BaseResponse changeTaskCenterExternalUserStatus(String corpId, String memberId, String taskUuid, String uuid,
                                                           String externalUserId, String status) {
        return null;
    }
}
