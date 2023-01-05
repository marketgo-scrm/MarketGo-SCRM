package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.biz.service.CronExpressionResolver;
import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.cron.PeriodEnum;
import com.easy.marketgo.common.utils.GenerateCronUtil;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import com.easy.marketgo.biz.service.wecom.usergroup.UserGroupMangerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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
    private UserGroupMangerService userGroupMangerService;

    public void queryWeComTaskCenterUserGroup(String taskType) {
        log.info("start to schedule task query user group send task center. taskType={}", taskType);
        checkRepeatTaskCenter(taskType);

        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComTaskCenterByScheduleTime(QUERY_USER_GROUP_TIME_BEFORE,
                        taskType, WeComMassTaskStatus.UNSTART.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.IMMEDIATE.getValue(),
                                WeComMassTaskScheduleType.FIXED_TIME.getValue()));
        log.info("schedule task query send task center. taskType={}, entities={}", taskType, entities);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("schedule task query task center is empty. taskType={}", taskType);
            return;
        }
        for (WeComTaskCenterEntity entity : entities) {
            if (!entity.getTaskStatus().equals(WeComMassTaskStatus.UNSTART.getValue())) {
                log.info("schedule task task type is error for task center. entity={}", entity);
                continue;
            }
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTING.getValue());

            startComputeUserGroup(entity);
        }
    }

    private void startComputeUserGroup(WeComTaskCenterEntity entity) {
        WeComUserGroupAudienceEntity weComUserGroupAudienceEntity =
                weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByUuid(entity.getUserGroupUuid());
        if (weComUserGroupAudienceEntity == null) {
            log.info("failed to query user group audience for task center. userGroupUuid={}",
                    entity.getUserGroupUuid());
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTE_FAILED.getValue());
            return;
        }

        if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLINE_USER_GROUP.getValue())) {
            String offlineConditions = weComUserGroupAudienceEntity.getOfflineConditions();
            queryUserGroupDetail(entity, offlineConditions);
        } else if (weComUserGroupAudienceEntity.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
            String cdpConditions = weComUserGroupAudienceEntity.getCdpConditions();
            queryUserGroupDetail(entity, cdpConditions);
        } else {
            String conditions = weComUserGroupAudienceEntity.getWecomConditions();
            queryUserGroupDetail(entity, conditions);
        }
    }

    private void queryUserGroupDetail(WeComTaskCenterEntity entity, String rules) {

        if (StringUtils.isBlank(rules)) {
            log.error("query user group conditions is empty. entity={}", entity);
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTE_FAILED.getValue());
            return;
        }

        WeComUserGroupAudienceEntity weComUserGroupAudienceEntity =
                weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByUuid(entity.getUserGroupUuid());
        try {
            userGroupMangerService.queryUserGroupDetail(entity.getProjectUuid(), entity.getCorpId(),
                    weComUserGroupAudienceEntity.getUserGroupType(), entity.getTaskType(), entity.getUuid(), rules);
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTED.getValue());
        } catch (Exception e) {
            log.error("failed to save send offline user group. error={}", e);
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTE_FAILED.getValue());
        }
    }

    private void checkRepeatTaskCenter(String taskType) {
        log.info("start query user group send task center for repeat task. taskType={}", taskType);
        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComTaskCenterByScheduleType(QUERY_USER_GROUP_TIME_BEFORE_REPEAT_TIME,
                        taskType, Arrays.asList(WeComMassTaskScheduleType.REPEAT_TIME.getValue()));
        log.info("query send task center. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query task center is empty. taskType={}", taskType);
            return;
        }

        for (WeComTaskCenterEntity entity : entities) {
            long startOfDay = DateUtil.beginOfDay(entity.getRepeatStartTime()).getTime();
            long endOfDay = DateUtil.endOfDay(entity.getRepeatEndTime()).getTime();
            long currentTime = System.currentTimeMillis();

            if (currentTime > endOfDay || currentTime < startOfDay) {
                log.info("query task center is not start or finish. currentTime={}, startTime={}, endTime={}, " +
                        "entity={}", currentTime, startOfDay, endOfDay, entity);
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
            if (currentTime + QUERY_USER_GROUP_TIME_BEFORE * 60 * 1000 < nextTime) {
                log.info("not execute time for task center. entity={}", entity);
                continue;
            }

            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTING.getValue());

            weComTaskCenterRepository.updateTaskExecuteTime(DateUtil.date(nextTime), entity.getUuid());

            startComputeUserGroup(entity);
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.COMPUTED.getValue());
        }
    }
}
