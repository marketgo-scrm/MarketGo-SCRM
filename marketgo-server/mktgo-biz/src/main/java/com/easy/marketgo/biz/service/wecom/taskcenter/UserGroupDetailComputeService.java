package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.biz.service.CronExpressionResolver;
import com.easy.marketgo.biz.service.wecom.QueryUserGroupDetailService;
import com.easy.marketgo.cdp.model.CrowdUsersBaseRequest;
import com.easy.marketgo.cdp.service.CdpManagerService;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.enums.cron.PeriodEnum;
import com.easy.marketgo.common.utils.GenerateCronUtil;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.bo.*;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
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
public class UserGroupDetailComputeService {

    private static final Integer QUERY_USER_GROUP_TIME_BEFORE = 10;

    private static final Integer QUERY_USER_GROUP_TIME_BEFORE_REPEAT_TIME = 2;

    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private UserGroupOfflineRepository userGroupOfflineRepository;

    @Autowired
    private CdpManagerService cdpManagerService;

    @Autowired
    private QueryUserGroupDetailService queryUserGroupDetailService;

    public void queryWeComTaskCenterUserGroup(String taskType) {
        log.info("start query user group send task center. taskType={}", taskType);
        checkRepeatTaskCenter(taskType);

        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComMassTaskByScheduleTime(QUERY_USER_GROUP_TIME_BEFORE,
                        taskType, WeComMassTaskStatus.UNSTART.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.IMMEDIATE.getValue(),
                                WeComMassTaskScheduleType.FIXED_TIME.getValue()));
        log.info("query send task center. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query task center is empty. taskType={}", taskType);
            return;
        }
        for (WeComTaskCenterEntity entity : entities) {
            if (!entity.getTaskStatus().equals(WeComMassTaskStatus.UNSTART.getValue())) {
                log.info("task type is error for task center. entity={}", entity);
                continue;
            }
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTING.getValue());


            WeComUserGroupAudienceEntity weComUserGroupAudienceEntity =
                    weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByUuid(entity.getUserGroupUuid());
            if (weComUserGroupAudienceEntity == null) {
                log.info("failed to query user group audience for task center. userGroupUuid={}",
                        entity.getUserGroupUuid());
                weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                continue;
            }

            if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLIEN_USER_GROUP.getValue())) {
                String offlineConditions = weComUserGroupAudienceEntity.getOfflineConditions();
                if (StringUtils.isBlank(offlineConditions)) {
                    log.error("query offline user group conditions is empty. weComUserGroupAudienceEntity={}",
                            weComUserGroupAudienceEntity);
                    weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    continue;
                }
                OfflineUserGroupAudienceRule offlineUserGroupAudienceRule = JsonUtils.toObject(offlineConditions,
                        OfflineUserGroupAudienceRule.class);
                queryOfflineUserGroup(entity.getCorpId(), offlineUserGroupAudienceRule.getUserGroupUuid(),
                        entity.getUuid());
                weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.COMPUTED.getValue());
            } else if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
                String cdpConditions = weComUserGroupAudienceEntity.getCdpConditions();
                if (StringUtils.isBlank(cdpConditions)) {
                    log.error("query cdp user group conditions is empty. weComUserGroupAudienceEntity={}",
                            weComUserGroupAudienceEntity);
                    weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
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
                    weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    continue;
                }

                WeComUserGroupAudienceRule weComUserGroupAudienceRule = JsonUtils.toObject(conditions,
                        WeComUserGroupAudienceRule.class);
                log.info("query user group for task center. weComUserGroupAudienceRule={}", weComUserGroupAudienceRule);
                try {
                    if (entity.getTaskType().equals(WeComMassTaskTypeEnum.GROUP.name())) {
                        queryGroupMassTaskUserGroup(entity.getCorpId(), entity.getUuid(), weComUserGroupAudienceRule);
                    } else if (entity.getTaskType().equals(WeComMassTaskTypeEnum.MOMENT.name())) {
                        queryMomentMassTaskUserGroup(entity.getCorpId(), entity.getUuid(), weComUserGroupAudienceRule);
                    } else {
                        List<String> memberList = queryUserGroupDetailService.getMemberList(entity.getCorpId(),
                                weComUserGroupAudienceRule);
                        log.info("query user group for member. memberList={}", memberList);
                        memberList = memberList.stream().distinct().collect(Collectors.toList());
                        log.info("query user group for member remove duplicate. memberList={}", memberList);
                        for (String member : memberList) {
                            List<WeComRelationMemberExternalUserEntity> externalUserEntities =
                                    queryUserGroupDetailService.getExternalUsers(entity.getCorpId(),
                                            Arrays.asList(member),
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
                    weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.COMPUTED.getValue());
                } catch (Exception e) {
                    log.error("failed to save mass task send queue. entity={}", entity, e);
                }
            }
        }
    }

    private void checkRepeatTaskCenter(String taskType) {
        log.info("start query user group send task center for repeat task. taskType={}", taskType);
        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComMassTaskByScheduleType(QUERY_USER_GROUP_TIME_BEFORE_REPEAT_TIME,
                        taskType, Arrays.asList(WeComMassTaskScheduleType.REPEAT_TIME.getValue()));
        log.info("query send task center. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query task center is empty. taskType={}", taskType);
            return;
        }

        for (WeComTaskCenterEntity entity : entities) {
            long startOfDay = DateUtil.endOfDay(entity.getRepeatStartTime()).getTime();
            long endOfDay = DateUtil.endOfDay(entity.getRepeatEndTime()).getTime();
            long currentTime = System.currentTimeMillis();

            if (currentTime > endOfDay || currentTime < startOfDay) {
                log.info("query task center is not start or finish. entity={}", entity);
                continue;
            }
            String[] startDate = DateUtil.formatDateTime(entity.getRepeatStartTime()).split(" ");
            String[] startTime = DateUtil.formatDateTime(entity.getScheduleTime()).split(" ");
            String cron = "";
            if (entity.getRepeatType().equals(PeriodEnum.DAILY.name())) {
                cron = GenerateCronUtil.INSTANCE.generateDailyCronByPeriodAndTime(startDate[0], startTime[1]);
            } else if (entity.getRepeatType().equals(PeriodEnum.WEEKLY.name())) {
                cron = GenerateCronUtil.INSTANCE.generateWeeklyCronByPeriodAndTime(startDate[0], startTime[1],
                        entity.getRepeatDay());
            } else if (entity.getRepeatType().equals(PeriodEnum.MONTHLY.name())) {
                cron = GenerateCronUtil.INSTANCE.generateMonthlyCronByPeriodAndTime(startDate[0], startTime[1],
                        entity.getRepeatDay());
            }

            log.info("compute cron string. cron={}", cron);

            CronExpressionResolver cronExpressionResolver = CronExpressionResolver.getInstance(cron);
            long nextTime = cronExpressionResolver.nextLongTime(currentTime);

            log.info("compute next time for cron string. cron={}, nextTime={}", cron, nextTime);

        }
    }

    private void queryOfflineUserGroup(String corpId, String userGroupUuid, String taskUuid) {
        try {
            List<String> memberIds = userGroupOfflineRepository.queryMemberByUuid(corpId, userGroupUuid);
            if (CollectionUtils.isEmpty(memberIds)) {
                log.info("query offline user group memberId is empty. corpId={}, uuid={}", corpId, userGroupUuid);
                weComTaskCenterRepository.updateTaskStatusByUUID(taskUuid,
                        WeComMassTaskStatus.COMPUTE_FAILED.getValue());
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
                    weComTaskCenterRepository.updateTaskStatusByUUID(taskUuid,
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

    private void queryGroupMassTaskUserGroup(String corpId, String taskUuid,
                                             WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        List<String> memberList = queryUserGroupDetailService.getMemberList(corpId, weComUserGroupAudienceRule);
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

    private void queryMomentMassTaskUserGroup(String corpId, String taskUuid,
                                              WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        List<String> memberList = new ArrayList<>();
        if (!weComUserGroupAudienceRule.getMembers().getIsAll()) {
            memberList = queryUserGroupDetailService.getMemberList(corpId, weComUserGroupAudienceRule);
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
