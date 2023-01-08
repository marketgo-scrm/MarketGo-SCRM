package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.core.model.bo.BaseResponse;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/14/22 4:30 PM
 * Describe:
 */
public interface ContactsManagerService {
    BaseResponse getExternalUserList(String projectId, Integer pageNum, Integer pageSize, String corpId,
                                     String externalUser,
                                     List<Integer> statuses,
                                     boolean duplicate,
                                     List<String> memberIds,
                                     List<Long> departments,
                                     List<String> tags,
                                     List<Integer> channels,
                                     List<String> groupChats,
                                     List<Integer> genders,
                                     String startTime,
                                     String endTime);

    BaseResponse getMemberList(String projectId, Integer pageNum, Integer pageSize, String corpId, Long departmentId,
                               String memberName);

    BaseResponse getGroupChatOwners(String projectId, String corpId);

    BaseResponse getGroupChatList(String projectId, String corpId, Integer pageNum, Integer pageSize, String groupChat,
                                  List<String> members);

    BaseResponse getExternalUserDetail(String corpId, String externalUserId, Integer pageNum, Integer pageSize);

    BaseResponse getGroupChatDetail(String corpId, String groupChat, Integer pageNum, Integer pageSize);

    BaseResponse getGroupChatStatistic(String corpId, String groupChat);

    BaseResponse getStatistic(String projectId, String corpId);

    BaseResponse getStatisticDetail(String projectId, String corpId, String dateType, Boolean holidaySwitch,String metricType,
                                    String startTime, String endTime);
}
