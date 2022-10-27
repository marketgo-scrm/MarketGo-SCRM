package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.web.model.response.BaseResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 4:50 PM
 * Describe:
 */

public interface WeComUserGroupService {

    BaseResponse estimate(String projectId, UserGroupAudienceRules audienceRules, String requestId,
                          String corpId, String taskType);

    BaseResponse queryUserGroup(String projectId, String corpId, String taskType, String groupUuid);
}
