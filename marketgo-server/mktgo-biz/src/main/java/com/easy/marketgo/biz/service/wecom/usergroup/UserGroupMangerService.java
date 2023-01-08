package com.easy.marketgo.biz.service.wecom.usergroup;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:15 PM
 * Describe:
 */
public interface UserGroupMangerService {

    void userGroupEstimate(String projectId, String requestId, String corpId, String taskType,
                           String userGroupRules);

    void queryUserGroupDetail(String projectId, String corpId, String userGroupType, String taskType, String taskUuid,
                              String userGroupRules);
}
