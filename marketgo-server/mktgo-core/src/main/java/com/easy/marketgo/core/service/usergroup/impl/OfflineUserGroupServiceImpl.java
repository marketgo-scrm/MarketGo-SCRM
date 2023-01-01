package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.usergroup.OfflineUserGroupAudienceRule;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.service.usergroup.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:17 PM
 * Describe:
 */
@Slf4j
@Component
public class OfflineUserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupOfflineRepository userGroupOfflineRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId, String taskType, String userGroupRules) {
        if (StringUtils.isBlank(userGroupRules)) {
            log.error("user group is empty for offline estimate result. requestId={}", requestId);
            return;
        }
        OfflineUserGroupAudienceRule offlineUserGroupAudienceRule = JsonUtils.toObject(userGroupRules,
                OfflineUserGroupAudienceRule.class);

        Integer memberCount = 0;
        Integer externalUserCount = 0;
        try {
            externalUserCount = userGroupOfflineRepository.queryExternalUserCountByUuid(corpId,
                    offlineUserGroupAudienceRule.getUserGroupUuid());
            memberCount = userGroupOfflineRepository.queryMemberCountByUuid(corpId,
                    offlineUserGroupAudienceRule.getUserGroupUuid());
        } catch (Exception e) {
            log.error("failed to user group for external user estimate result. requestId={}", requestId, e);
        }
        UserGroupEstimateResult userGroupEstimateResult = new UserGroupEstimateResult();
        userGroupEstimateResult.setExternalUserCount(externalUserCount);
        userGroupEstimateResult.setMemberCount(memberCount);

        weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                JsonUtils.toJSONString(userGroupEstimateResult),
                UserGroupAudienceStatusEnum.SUCCEED.getValue());
    }

}
