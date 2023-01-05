package com.easy.marketgo.biz.service.wecom.usergroup.impl;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.contacts.WeComCustomerService;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.biz.service.wecom.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:30 PM
 * Describe:
 */
@Slf4j
@Component
public class WeComSingleUserGroupServiceImpl implements WeComUserGroupService {

    @Autowired
    private WeComMemberService weComMemberService;

    @Autowired
    private WeComCustomerService weComCustomerService;

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
                    weComCustomerService.buildQueryExternalUserSqlParam(corpId, distinctMemberList,
                            weComUserGroupAudienceRule, null, null);

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
        log.info("query weCom user group for member. memberList={}", memberList);
        for (String member : memberList) {
            List<WeComRelationMemberExternalUserEntity> externalUserEntities =
                    weComCustomerService.getExternalUsers(corpId, Arrays.asList(member), userGroupRules);
            log.info("weCom user group for external user estimate total result. externalUserCount={}",
                    externalUserEntities.size());

            Map<String, List<String>> memberRelationExternalUsers =
                    externalUserEntities.stream().collect(Collectors.groupingBy(WeComRelationMemberExternalUserEntity::getMemberId,
                            Collectors.mapping(WeComRelationMemberExternalUserEntity::getExternalUserId,
                                    Collectors.toList())));
            log.info("weCom user group query external user list result. member={}, externalUser size={}", member,
                    memberRelationExternalUsers.size());
            List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities = new ArrayList<>();
            Iterator<Map.Entry<String, List<String>>> iterator =
                    memberRelationExternalUsers.entrySet().iterator();
            while (iterator.hasNext()) {
                WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity =
                        new WeComMassTaskSendQueueEntity();
                Map.Entry<String, List<String>> item = iterator.next();
                weComMassTaskSendQueueEntity.setMemberId(item.getKey());
                weComMassTaskSendQueueEntity.setUuid(UuidUtils.generateUuid());
                weComMassTaskSendQueueEntity.setMemberMd5(SecureUtil.md5(item.getKey()));
                weComMassTaskSendQueueEntity.setTaskUuid(taskUuid);
                weComMassTaskSendQueueEntity.setExternalUserIds(item.getValue().stream().collect(Collectors.joining(
                        ",")));
                weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
                log.info("weCom save task send queue. weComMassTaskSendQueueEntity={}", weComMassTaskSendQueueEntity);
                weComMassTaskSendQueueEntities.add(weComMassTaskSendQueueEntity);
            }
            weComMassTaskSendQueueRepository.saveAll(weComMassTaskSendQueueEntities);
        }
    }
}
