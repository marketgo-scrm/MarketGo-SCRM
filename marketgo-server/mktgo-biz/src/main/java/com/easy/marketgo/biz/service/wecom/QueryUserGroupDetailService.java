package com.easy.marketgo.biz.service.wecom;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.model.cdp.CrowdUsersBaseRequest;
import com.easy.marketgo.core.model.usergroup.CdpUserGroupAudienceRule;
import com.easy.marketgo.core.model.usergroup.OfflineUserGroupAudienceRule;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.cdp.CdpManagerService;
import com.easy.marketgo.core.service.contacts.WeComCustomerService;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/28/22 9:58 PM
 * Describe:
 */
@Slf4j
@Service
public class QueryUserGroupDetailService {

    private static final Integer QUERY_USER_GROUP_TIME_BEFORE = 10;
    @Autowired
    private WeComMassTaskRepository weComMassTaskRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private UserGroupOfflineRepository userGroupOfflineRepository;

    @Autowired
    private CdpManagerService cdpManagerService;

    @Autowired
    private WeComMemberService weComMemberService;

    @Autowired
    private WeComCustomerService weComCustomerService;

    public void queryWeComMassTaskUserGroup(String taskType) {
//        Date date = DateUtil.date();
//        Date newDate = DateUtil.offset(date, DateField.MINUTE, 2);
//        LocalDateTime now = LocalDateTime.now();
//        String nowTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        weComMassTaskRepository.querytByStatusAndScheduleTime(WeComMassTaskTypeEnum.SINGLE.name(),
//                WeComMassTaskStatus.UNSTART.getValue(), nowTime);
        log.info("start query user group send mass task. taskType={}", taskType);
        List<WeComMassTaskEntity> entities =
                weComMassTaskRepository.getWeComMassTaskByScheduleTime(QUERY_USER_GROUP_TIME_BEFORE,
                        taskType, WeComMassTaskStatus.UNSTART.getValue());
        log.info("query send mass task. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query mass task is empty. taskType={}", taskType);
            return;
        }
        for (WeComMassTaskEntity entity : entities) {
            if (!entity.getTaskStatus().equals(WeComMassTaskStatus.UNSTART.getValue())) {
                log.info("task type is error for mass task. entity={}", entity);
                continue;
            }
            weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTING.getValue());


            WeComUserGroupAudienceEntity weComUserGroupAudienceEntity =
                    weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByUuid(entity.getUserGroupUuid());
            if (weComUserGroupAudienceEntity == null) {
                log.info("failed to query user group audience. userGroupUuid={}", entity.getUserGroupUuid());
                weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                continue;
            }

            if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLINE_USER_GROUP.getValue())) {
                String offlineConditions = weComUserGroupAudienceEntity.getOfflineConditions();
                if (StringUtils.isBlank(offlineConditions)) {
                    log.error("query offline user group conditions is empty. weComUserGroupAudienceEntity={}",
                            weComUserGroupAudienceEntity);
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    continue;
                }
                OfflineUserGroupAudienceRule offlineUserGroupAudienceRule = JsonUtils.toObject(offlineConditions,
                        OfflineUserGroupAudienceRule.class);
                queryOfflineUserGroup(entity.getCorpId(), offlineUserGroupAudienceRule.getUserGroupUuid(),
                        entity.getUuid());
                weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.COMPUTED.getValue());
            } else if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
                String cdpConditions = weComUserGroupAudienceEntity.getCdpConditions();
                if (StringUtils.isBlank(cdpConditions)) {
                    log.error("query cdp user group conditions is empty. weComUserGroupAudienceEntity={}",
                            weComUserGroupAudienceEntity);
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    continue;
                }

                CdpUserGroupAudienceRule cdpUserGroupAudienceRule = JsonUtils.toObject(cdpConditions,
                        CdpUserGroupAudienceRule.class);
                CrowdUsersBaseRequest request = new CrowdUsersBaseRequest();
                request.setProjectUuid(weComUserGroupAudienceEntity.getProjectUuid());
                request.setCdpType(cdpUserGroupAudienceRule.getCdpType());
                request.setCorpId(entity.getCorpId());
                List<CrowdUsersBaseRequest.CrowdMessage> crowList = new ArrayList<>();
                for (CdpUserGroupAudienceRule.CrowdMessage message : cdpUserGroupAudienceRule.getCrowds()) {
                    CrowdUsersBaseRequest.CrowdMessage crowdMessage = new CrowdUsersBaseRequest.CrowdMessage();
                    BeanUtils.copyProperties(message, crowdMessage);
                    crowList.add(crowdMessage);
                }
                cdpManagerService.queryCrowdUsers(request);
            } else {

                String conditions = weComUserGroupAudienceEntity.getWecomConditions();
                if (StringUtils.isBlank(conditions)) {
                    log.error("query user group conditions is empty. weComUserGroupAudienceEntity={}",
                            weComUserGroupAudienceEntity);
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    continue;
                }

                WeComUserGroupAudienceRule weComUserGroupAudienceRule = JsonUtils.toObject(conditions,
                        WeComUserGroupAudienceRule.class);
                log.info("query user group for mass task. weComUserGroupAudienceRule={}", weComUserGroupAudienceRule);
                try {
                    if (entity.getTaskType().equals(WeComMassTaskTypeEnum.GROUP.name())) {
                        queryGroupMassTaskUserGroup(entity.getCorpId(), entity.getUuid(), weComUserGroupAudienceRule);
                    } else if (entity.getTaskType().equals(WeComMassTaskTypeEnum.MOMENT.name())) {
                        queryMomentMassTaskUserGroup(entity.getCorpId(), entity.getUuid(), weComUserGroupAudienceRule);
                    } else {
                        List<String> memberList = weComMemberService.getMemberList(entity.getCorpId(), weComUserGroupAudienceRule);
                        log.info("query user group for member. memberList={}", memberList);
                        memberList = memberList.stream().distinct().collect(Collectors.toList());
                        log.info("query user group for member remove duplicate. memberList={}", memberList);
                        for (String member : memberList) {
                            List<WeComRelationMemberExternalUserEntity> externalUserEntities =
                                    weComCustomerService.getExternalUsers(entity.getCorpId(), Arrays.asList(member),
                                            weComUserGroupAudienceRule);
                            log.info("user group for external user estimate total result. externalUserCount={}",
                                    externalUserEntities.size());

                            Map<String, List<String>> memberRelationExternalUsers =
                                    externalUserEntities.stream().collect(Collectors.groupingBy(WeComRelationMemberExternalUserEntity::getMemberId,
                                            Collectors.mapping(WeComRelationMemberExternalUserEntity::getExternalUserId,
                                                    Collectors.toList())));
                            log.info("memberRelationExternalUsers result. memberRelationExternalUsers={}",
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
                                weComMassTaskSendQueueEntity.setTaskUuid(entity.getUuid());
                                weComMassTaskSendQueueEntity.setExternalUserIds(item.getValue().stream().collect(Collectors.joining(",")));
                                weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
                                log.info("save mass task send queue. weComMassTaskSendQueueEntity={}",
                                        weComMassTaskSendQueueEntity);
                                weComMassTaskSendQueueEntities.add(weComMassTaskSendQueueEntity);
                            }

                            weComMassTaskSendQueueRepository.saveAll(weComMassTaskSendQueueEntities);
                        }
                    }
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTED.getValue());
                } catch (Exception e) {
                    log.info("failed to save mass task send queue. entity={}", entity, e);
                }
            }
        }
    }

    public void queryOfflineUserGroup(String corpId, String userGroupUuid, String taskUuid) {
        try {
            List<String> memberIds = userGroupOfflineRepository.queryMemberByUuid(corpId, userGroupUuid);
            if (CollectionUtils.isEmpty(memberIds)) {
                log.info("query offline user group memberId is empty. corpId={}, uuid={}", corpId, userGroupUuid);
                weComMassTaskRepository.updateTaskStatusByUUID(taskUuid, WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                return;
            }
            log.info("query offline user group memberId count={}, corpId={}, uuid={}", memberIds.size(), corpId,
                    userGroupUuid);
            for (String memberId : memberIds) {
                List<String> externalUsers = userGroupOfflineRepository.queryExternalUsersByUuidAndMemberId(corpId,
                        userGroupUuid, memberId);
                if (CollectionUtils.isEmpty(externalUsers)) {
                    log.info("query offline user group externalUser is empty. corpId={}, uuid={},  memberId={}", corpId,
                            userGroupUuid, memberId);
                    weComMassTaskRepository.updateTaskStatusByUUID(taskUuid,
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    return;
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
        } catch (Exception e) {
            log.error("failed to save send offline user group. error={}", e);
        }
    }

    public void queryGroupMassTaskUserGroup(String corpId, String taskUuid,
                                             WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        List<String> memberList = weComMemberService.getMemberList(corpId, weComUserGroupAudienceRule);
        memberList = memberList.stream().distinct().collect(Collectors.toList());
        List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities = new ArrayList<>();
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
        weComMassTaskSendQueueRepository.saveAll(weComMassTaskSendQueueEntities);
    }

    public void queryMomentMassTaskUserGroup(String corpId, String taskUuid,
                                              WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        List<String> memberList = new ArrayList<>();
        if (!weComUserGroupAudienceRule.getMembers().getIsAll()) {
            memberList = weComMemberService.getMemberList(corpId, weComUserGroupAudienceRule);
        } else {
            memberList = weComGroupChatsRepository.queryOwnersByCorpId(corpId);
        }
        WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity = new WeComMassTaskSendQueueEntity();
        if (CollectionUtils.isNotEmpty(memberList)) {
            String members = memberList.stream().collect(Collectors.joining(","));
            weComMassTaskSendQueueEntity.setMemberMd5(SecureUtil.md5(members));
            weComMassTaskSendQueueEntity.setMemberId(members);
        }

        weComMassTaskSendQueueEntity.setUuid(UuidUtils.generateUuid());

        weComMassTaskSendQueueEntity.setTaskUuid(taskUuid);

        if (!weComUserGroupAudienceRule.getExternalUsers().getIsAll() &&
                weComUserGroupAudienceRule.getExternalUsers().isCorpTagSwitch()
                && weComUserGroupAudienceRule.getExternalUsers().getCorpTags() != null &&
                CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags())) {
            List<String> tags = new ArrayList<>();
            weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags().forEach(tag -> {
                tags.add(tag.getId());
            });
            weComMassTaskSendQueueEntity.setExternalUserIds(tags.stream().collect(Collectors.joining(",")));
        }

        weComMassTaskSendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
        weComMassTaskSendQueueRepository.save(weComMassTaskSendQueueEntity);
    }
}
