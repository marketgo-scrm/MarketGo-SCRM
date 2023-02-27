package com.easy.marketgo.biz.service.wecom.usergroup.impl;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.usergroup.OfflineUserGroupAudienceRule;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.model.usergroup.UserGroupRules;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.biz.service.wecom.usergroup.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId, String taskType,
                                  UserGroupRules userGroupRules) {

        OfflineUserGroupAudienceRule offlineUserGroupAudienceRule = userGroupRules.getOfflineUserGroupRule();
        if (offlineUserGroupAudienceRule == null) {
            log.error("offline user group is empty for estimate result. requestId={}", requestId);
            return;
        }
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

    @Override
    public void queryUserGroupDetail(String projectId, String corpId, String taskType,
                                     String taskUuid, String userGroupRule) {
        OfflineUserGroupAudienceRule offlineUserGroupAudienceRule = JsonUtils.toObject(userGroupRule,
                OfflineUserGroupAudienceRule.class);
        queryOfflineUserGroup(corpId, offlineUserGroupAudienceRule.getUserGroupUuid(),
                taskUuid);
    }

    private void queryOfflineUserGroup(String corpId, String userGroupUuid, String taskUuid) {
        List<String> memberIds = userGroupOfflineRepository.queryMemberByUuid(corpId, userGroupUuid);
        if (CollectionUtils.isEmpty(memberIds)) {
            log.info("query offline user group memberId is empty. corpId={}, uuid={}", corpId, userGroupUuid);
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_OFFLINE_USER_GROUP_COMPUTE_FAILED);
        }
        log.info("query offline user group memberId count={}, corpId={}, uuid={}", memberIds.size(), corpId,
                userGroupUuid);
        for (String memberId : memberIds) {
            List<String> externalUsers = userGroupOfflineRepository.queryExternalUsersByUuidAndMemberId(corpId,
                    userGroupUuid, memberId);
            if (CollectionUtils.isEmpty(externalUsers)) {
                log.info("query offline user group externalUser is empty. corpId={}, uuid={},  memberId={}", corpId,
                        userGroupUuid, memberId);
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_OFFLINE_USER_GROUP_COMPUTE_FAILED);
            }
            log.info("query offline user group externalUser count={}, corpId={}, uuid={}, memberId={}",
                    externalUsers.size(), corpId, userGroupUuid, memberId);

            WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity = new WeComMassTaskSendQueueEntity();
            weComMassTaskSendQueueEntity.setMemberId(memberId);
            weComMassTaskSendQueueEntity.setUuid(UuidUtils.generateUuid());
            weComMassTaskSendQueueEntity.setMemberMd5(SecureUtil.md5(memberId));
            weComMassTaskSendQueueEntity.setTaskUuid(taskUuid);
            weComMassTaskSendQueueEntity.setExternalUserIds(externalUsers.stream().collect(Collectors.joining(",")));
            weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
            log.info("save mass task send queue. weComMassTaskSendQueueEntity={}", weComMassTaskSendQueueEntity);
            weComMassTaskSendQueueRepository.save(weComMassTaskSendQueueEntity);
        }

    }
}
