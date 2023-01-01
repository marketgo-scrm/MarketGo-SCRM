package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.usergroup.UserGroupRules;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.service.usergroup.UserGroupService;
import com.easy.marketgo.core.service.usergroup.WeComTaskTypeStrategyFactory;
import com.easy.marketgo.core.service.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:16 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComUserGroupManagerImpl implements UserGroupService {

    @Autowired
    private WeComTaskTypeStrategyFactory weComTaskTypeStrategyFactory;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId, String taskType,
                                  UserGroupRules userGroupRules) {

        WeComUserGroupAudienceRule weComUserGroupAudienceRule = userGroupRules.getWeComUserGroupRule();
        if (weComUserGroupAudienceRule == null) {
            log.error("weCom user group is empty for estimate result. requestId={}", requestId);
            return;
        }
        log.info("start to weCom user group estimate. requestId={}, corpId={}, taskType={}, " +
                "weComUserGroupAudienceRule={}", requestId, corpId, taskType, weComUserGroupAudienceRule);
        WeComUserGroupService weComUserGroupService = weComTaskTypeStrategyFactory.getUserGroupTaskService(taskType);
        weComUserGroupService.userGroupEstimate(projectId, corpId, requestId, weComUserGroupAudienceRule);
    }
}
