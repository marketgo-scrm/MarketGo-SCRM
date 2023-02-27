package com.easy.marketgo.biz.service.wecom.usergroup.impl;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.taskcenter.QueryGroupChatsBuildSqlParam;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.contacts.WeComCustomerService;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.biz.service.wecom.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:31 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComGroupUserGroupServiceImpl implements WeComUserGroupService {

    @Autowired
    private WeComMemberService weComMemberService;

    @Autowired
    private WeComCustomerService weComCustomerService;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

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

            QueryGroupChatsBuildSqlParam paramBuilder =
                    weComCustomerService.buildQueryGroupChatsSqlParam(corpId, distinctMemberList,
                            weComUserGroupAudienceRule);

            externalUserCount = weComGroupChatsRepository.countByCnd(paramBuilder);
            log.info("user group to query group chat count. groupChatCount={}", externalUserCount);

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

        List<String> memberList = new ArrayList<>();
        if (!userGroupRules.getMembers().getIsAll()) {
            memberList = weComMemberService.getMemberList(corpId, userGroupRules);
        } else {
            memberList = weComGroupChatsRepository.queryOwnersByCorpId(corpId);
        }
        QueryGroupChatsBuildSqlParam param = weComCustomerService.buildQueryGroupChatsSqlParam(corpId, memberList,
                userGroupRules);
        List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities = new ArrayList<>();
        if (param != null) {
            List<WeComGroupChatsEntity> entities = weComGroupChatsRepository.listGroupChatsByCnd(param);
            Map<String, List<String>> memberRelationGroupChats =
                    entities.stream().collect(Collectors.groupingBy(WeComGroupChatsEntity::getOwner,
                            Collectors.mapping(WeComGroupChatsEntity::getGroupChatId,
                                    Collectors.toList())));
            Iterator<Map.Entry<String, List<String>>> iterator =
                    memberRelationGroupChats.entrySet().iterator();
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
        } else {
            memberList.forEach(member -> {
                WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity = new WeComMassTaskSendQueueEntity();
                weComMassTaskSendQueueEntity.setUuid(UuidUtils.generateUuid());
                weComMassTaskSendQueueEntity.setMemberMd5(member);
                weComMassTaskSendQueueEntity.setMemberId(member);
                weComMassTaskSendQueueEntity.setTaskUuid(taskUuid);
                weComMassTaskSendQueueEntity.setExternalUserIds("");
                weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
                weComMassTaskSendQueueEntities.add(weComMassTaskSendQueueEntity);
            });
        }
        weComMassTaskSendQueueRepository.saveAll(weComMassTaskSendQueueEntities);
    }
}
