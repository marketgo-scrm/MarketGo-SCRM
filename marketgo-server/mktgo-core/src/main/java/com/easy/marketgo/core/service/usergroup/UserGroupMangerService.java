package com.easy.marketgo.core.service.usergroup;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:15 PM
 * Describe:
 */
public interface UserGroupMangerService {

    void userGroupEstimate(String projectId, String requestId, String corpId, String userGroupType, String taskType,
                           String userGroupRules);
}
