package com.easy.marketgo.biz.service.wecom.usergroup;

import com.easy.marketgo.core.model.usergroup.UserGroupRules;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/25/22 5:43 PM
 * Describe:
 */
public interface UserGroupService {
    void userGroupEstimate(String projectId, String corpId, String requestId, String taskType,
                           UserGroupRules userGroupRules);

    void queryUserGroupDetail(String projectId, String corpId, String taskType, String taskUuid, String userGroupRule);
}
