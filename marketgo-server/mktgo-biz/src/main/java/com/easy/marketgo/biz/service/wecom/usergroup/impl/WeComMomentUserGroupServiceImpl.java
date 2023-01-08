package com.easy.marketgo.biz.service.wecom.usergroup.impl;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.biz.service.wecom.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:31 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComMomentUserGroupServiceImpl implements WeComUserGroupService {

    @Autowired
    private WeComMemberService weComMemberService;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId,
                                  WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        Integer memberCount = 0;
        Integer externalUserCount = 0;
        try {
            List<String> distinctMemberList = weComMemberService.getMemberList(corpId, weComUserGroupAudienceRule);
            memberCount = distinctMemberList.size();
            log.info("user group for member estimate result. memberCount={}, distinctMemberList={}", memberCount,
                    distinctMemberList);

            QueryUserGroupBuildSqlParam paramBuilder =
                    QueryUserGroupBuildSqlParam.builder().corpId(corpId).memberIds(distinctMemberList).build();
            paramBuilder.setRelation(weComUserGroupAudienceRule.getExternalUsers().getRelation());
            if (!weComUserGroupAudienceRule.getExternalUsers().getIsAll()) {

                //标签条件
                if (weComUserGroupAudienceRule.getExternalUsers().isCorpTagSwitch()
                        && weComUserGroupAudienceRule.getExternalUsers().getCorpTags() != null && CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags())) {
                    paramBuilder.setTagRelation(weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getRelation());
                    List<String> tags = new ArrayList<>();
                    weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                        tags.add(weComCorpTag.getId());
                    });

                    paramBuilder.setTags(tags);
                }
            }
            externalUserCount = weComRelationMemberExternalUserRepository.countByUserGroupCnd(paramBuilder);
            log.info("user group for external user estimate result. externalUserCount={}", externalUserCount);
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
    public void queryUserGroupDetail(String projectId, String corpId, String taskUuid,
                                     WeComUserGroupAudienceRule userGroupRules) {

        List<String> memberList = weComMemberService.getMemberList(corpId, userGroupRules);

        WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity = new WeComMassTaskSendQueueEntity();
        if (CollectionUtils.isNotEmpty(memberList)) {
            String members = memberList.stream().collect(Collectors.joining(","));
            weComMassTaskSendQueueEntity.setMemberMd5(SecureUtil.md5(members));
            weComMassTaskSendQueueEntity.setMemberId(members);
        }

        weComMassTaskSendQueueEntity.setUuid(UuidUtils.generateUuid());

        weComMassTaskSendQueueEntity.setTaskUuid(taskUuid);

        if (!userGroupRules.getExternalUsers().getIsAll() &&
                userGroupRules.getExternalUsers().isCorpTagSwitch() &&
                userGroupRules.getExternalUsers().getCorpTags() != null &&
                CollectionUtils.isNotEmpty(userGroupRules.getExternalUsers().getCorpTags().getTags())) {
            List<String> tags = new ArrayList<>();
            userGroupRules.getExternalUsers().getCorpTags().getTags().forEach(tag -> {
                tags.add(tag.getId());
            });
            weComMassTaskSendQueueEntity.setExternalUserIds(tags.stream().collect(Collectors.joining(",")));
        }

        weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
        weComMassTaskSendQueueRepository.save(weComMassTaskSendQueueEntity);

    }
}
