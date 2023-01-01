package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.usergroup.UserGroupRules;
import com.easy.marketgo.core.service.usergroup.UserGroupMangerService;
import com.easy.marketgo.core.service.usergroup.UserGroupService;
import com.easy.marketgo.core.service.usergroup.WeComUserGroupStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:16 PM
 * Describe:
 */

@Slf4j
@Component
public class UserGroupMangerServiceImpl implements UserGroupMangerService {

    @Autowired
    private WeComUserGroupStrategyFactory weComUserGroupStrategyFactory;

    @Override
    public void userGroupEstimate(String projectId, String requestId, String corpId,
                                  String taskType, String userGroupRules) {
        startUserGroupEstimate(projectId, requestId, corpId, taskType, userGroupRules);
    }

    @Async
    public void startUserGroupEstimate(String projectId, String requestId, String corpId,
                                       String taskType, String userGroupRules) {
        log.info("start to user group estimate. requestId={}, corpId={}, taskType={}, " +
                "userGroupRules={}", requestId, corpId, taskType, userGroupRules);
        UserGroupRules rules = JsonUtils.toObject(userGroupRules, UserGroupRules.class);
        String userGroupType = rules.getUserGroupType();
        UserGroupService userGroupService = weComUserGroupStrategyFactory.getUserGroupService(userGroupType);
        userGroupService.userGroupEstimate(projectId, corpId, requestId, taskType, rules);
    }
}
