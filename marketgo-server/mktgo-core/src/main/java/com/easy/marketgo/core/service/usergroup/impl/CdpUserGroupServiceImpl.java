package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.usergroup.CdpUserGroupAudienceRule;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.model.usergroup.UserGroupRules;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.service.usergroup.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
public class CdpUserGroupServiceImpl implements UserGroupService {

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId, String taskType, UserGroupRules userGroupRules) {

        CdpUserGroupAudienceRule cdpUserGroupAudienceRule = userGroupRules.getCdpUserGroupRule();
        if (cdpUserGroupAudienceRule == null) {
            log.error("cdp user group is empty for estimate result. requestId={}", requestId);
            return;
        }
        Integer memberCount = 0;
        Integer externalUserCount = 0;
        try {
            if (CollectionUtils.isNotEmpty(cdpUserGroupAudienceRule.getCrowds())) {
                for (CdpUserGroupAudienceRule.CrowdMessage message : cdpUserGroupAudienceRule.getCrowds()) {
                    memberCount += message.getUserCount();
                    externalUserCount += message.getUserCount();
                }
            }
        } catch (Exception e) {
            log.error("failed to user group for cdp estimate result. requestId={}", requestId, e);
        }
        UserGroupEstimateResult userGroupEstimateResult = new UserGroupEstimateResult();
        userGroupEstimateResult.setExternalUserCount(externalUserCount);
        userGroupEstimateResult.setMemberCount(memberCount);

        weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                JsonUtils.toJSONString(userGroupEstimateResult),
                UserGroupAudienceStatusEnum.SUCCEED.getValue());
    }
}
