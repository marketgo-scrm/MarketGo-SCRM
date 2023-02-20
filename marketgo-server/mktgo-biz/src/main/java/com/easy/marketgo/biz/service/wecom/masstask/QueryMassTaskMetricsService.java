package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.*;
import com.easy.marketgo.api.model.response.*;
import com.easy.marketgo.api.model.response.masstask.*;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSyncStatisticEntity;
import com.easy.marketgo.core.model.bo.WeComMassTaskMetricsBO;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSyncStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;


/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/17/22 3:46 PM
 * Describe:
 */
@Slf4j
@Service
public class QueryMassTaskMetricsService {

    public static final int CREATE_MOMENT_STATUE_COMPLETE = 3;

    private static final int MEMBER_STATUS_UNSENT = 0;
    private static final int MEMBER_STATUS_SENT = 2;

    private static final int MEMBER_STATUS_PUBLISH = 1;

    private static final int EXTERNAL_USER_STATUS_UNDELIVERED = 0;
    private static final int EXTERNAL_USER_STATUS_DELIVERED = 1;
    private static final int EXTERNAL_USER_STATUS_UNFRIEND = 2;
    private static final int EXTERNAL_USER_STATUS_EXCEED_LIMIT = 3;

    private static final int QUERY_STATUS_FINISH = 1;
    private static final int QUERY_STATUS_CONTINUE = 0;

    // 60分钟还在计算同步的任务就重置状态
    private static final int MAX_TIME_FOR_RUNNING_MS = 30 * 60 * 1000;

    @Resource
    private WeComMassTaskRpcService weComMassTaskRpcService;

    @Autowired
    private RabbitTemplate weComMassTaskStatisticAmqpTemplate;

    @Autowired
    private WeComMassTaskSyncStatisticRepository weComMassTaskSyncStatisticRepository;

    @Autowired
    private WeComMassTaskRepository weComMassTaskRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComMassTaskMemberStatisticRepository weComMassTaskMemberStatisticRepository;

    private ExecutorService executorService;

    private RateLimiter rateLimiter = RateLimiter.create(2);

    private static final Integer CAPACITY_ONE = 1000;
    private static final Integer CORE_THREAD_NUM = 10;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("wx_query_mass_task_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    public void checkMassTaskSyncStatus() {
        log.info("start query mass task metrics for sync.");
        int offset = 0;
        int limitSize = 100;
        do {
            log.info("query mass task metrics for db.offset={}, limitSize={}", offset, limitSize);
            List<WeComMassTaskSyncStatisticEntity> messages =
                    weComMassTaskSyncStatisticRepository.getWeComMassTaskResponseBySyncStatus(
                            WeComMassTaskSyncMetricsStatus.RUNNING.name(), offset, limitSize);
            for (WeComMassTaskSyncStatisticEntity item : messages) {
                log.info("check sync status for update time.taskMessage={}", item);
                if (System.currentTimeMillis() - item.getUpdateTime().getTime() < MAX_TIME_FOR_RUNNING_MS) {
                    continue;
                }
                log.info("reset sync status because the max time is exceeded.time={},taskMessage={}",
                        System.currentTimeMillis() - item.getUpdateTime().getTime() < MAX_TIME_FOR_RUNNING_MS, item);
                weComMassTaskSyncStatisticRepository.resetWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus.RUNNING.name(),
                        WeComMassTaskSyncMetricsStatus.INIT.name(), item.getId());
            }
            offset = (messages.size() < limitSize) ? 0 : (offset + limitSize);
        } while (offset > 0);
    }

    public void checkMassTaskStatus() {
        log.info("start check mass task for moment status.");
        int offset = 0;
        int limitSize = 100;
        do {
            log.info("query mass task for moment status from db.offset={}, limitSize={}", offset, limitSize);
            List<WeComMassTaskSyncStatisticEntity> messages =
                    weComMassTaskSyncStatisticRepository.getWeComMassTaskResponseByCreateTime(
                            Constants.MAX_TIME_FOR_MOMENT_SYNC, WeComMassTaskTypeEnum.MOMENT.name(),
                            offset, limitSize);
            for (WeComMassTaskSyncStatisticEntity item : messages) {
                WeComMassTaskEntity weComMassTask = weComMassTaskRepository.getByTaskUUID(item.getTaskUuid());
                if (weComMassTask != null && weComMassTask.getFinishTime() != null) {
                    log.info("check mass task status  for create time.finishTime={}, currentTime={}, taskMessage={}",
                            weComMassTask.getFinishTime().getTime(), System.currentTimeMillis(), item);
                    if (weComMassTask.getFinishTime().getTime() > System.currentTimeMillis()) {
                        continue;
                    }
                }
                log.info("set mass task because the finish time is exceeded.taskMessage={}", item);
                weComMassTaskSyncStatisticRepository.deleteByTaskUuidAndSendId(item.getTaskUuid(), item.getSendId());
            }
            offset = (messages.size() < limitSize) ? 0 : (offset + limitSize);
        } while (offset > 0);

        List<WeComMassTaskEntity> weComMassTaskEntities =
                weComMassTaskRepository.getByTaskStatus(WeComMassTaskStatus.SENT.getValue());
        for (WeComMassTaskEntity entity : weComMassTaskEntities) {
            int count = weComMassTaskSyncStatisticRepository.countBySyncStatusAndTaskUuid(entity.getUuid());
            log.info("query mass task sync status. count={}, taskUuid={}", count, entity.getUuid());
            if (count == 0) {
                weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.FINISHED.getValue());
                weComMassTaskMemberStatisticRepository.updateMemberStatusBytaskUuid(WeComMassTaskMemberStatusEnum.UNSENT.getValue(), WeComMassTaskMemberStatusEnum.SENT_FAIL.getValue(), entity.getUuid());
            }
        }
    }

    /**
     * 开始查询群发任务给个人的统计信息
     */
    public void startQueryMassTaskMetrics(String taskType, String resultType) {
        int offset = 0;
        int limitSize = 100;
        Map<String, WeComCorpMessage> mapWeComCorpMessage = new HashMap<>();

        do {
            log.info("start query mass task metrics.taskType={}, sendIdType={}, offset={}, limitSize={}", taskType,
                    resultType, offset, limitSize);
            List<WeComMassTaskSyncStatisticEntity> messages =
                    weComMassTaskSyncStatisticRepository.queryWeComMassTaskSendIdByTaskType(WeComMassTaskSyncMetricsStatus.RUNNING.name(),
                            taskType, resultType, offset, limitSize);
            if (!CollectionUtils.isNotEmpty(messages)) {
                log.info("query mass task is empty.taskType={}, sendIdType={}", taskType, resultType);
                continue;
            }
            String projectUuid = "";
            log.info("start query mass task from mysql.messages={}", messages);
            for (WeComMassTaskSyncStatisticEntity item : messages) {
                WeComCorpMessage message = mapWeComCorpMessage.get(item.getTaskUuid());
                if (message == null) {
                    WeComMassTaskEntity weComMassTask = weComMassTaskRepository.getByTaskUUID(item.getTaskUuid());
                    if (weComMassTask == null ||
                            (!weComMassTask.getTaskType().equalsIgnoreCase(WeComMassTaskTypeEnum.MOMENT.name()) && weComMassTask.getTaskType().equalsIgnoreCase(
                                    WeComMassTaskStatus.FINISHED.name()))) {
                        log.info("failed to query corpId and agentId from mysql or mass task is finished.taskUuid={}",
                                item.getTaskUuid());
                        continue;
                    }
                    projectUuid = weComMassTask.getProjectUuid();
                    log.info("start query corpId and agentId from mysql.weComMassTask={}", weComMassTask);
                    WeComCorpMessage corpMessage = new WeComCorpMessage();
                    corpMessage.setCorpId(weComMassTask.getCorpId());
                    WeComAgentMessageEntity agentMessageEntity =
                            weComAgentMessageRepository.getWeComAgentByCorp(weComMassTask.getProjectUuid(),
                                    weComMassTask.getCorpId());
                    log.info("query agent message for mass task. agentMessageEntity={}", agentMessageEntity);
                    String agentId = (agentMessageEntity == null ? "" : agentMessageEntity.getAgentId());

                    corpMessage.setAgentId(agentId);
                    mapWeComCorpMessage.put(item.getTaskUuid(), corpMessage);
                }
                WeComCorpMessage corpMessage = mapWeComCorpMessage.get(item.getTaskUuid());
                Runnable task =
                        new WeComQueryMemberMetrics(item.getId(), projectUuid, item.getTaskUuid(),
                                item.getTaskType().name(), item.getSendIdType().name(), item.getSendId(),
                                corpMessage.getCorpId(), corpMessage.getAgentId());
                weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus.RUNNING.name(),
                        item.getId());
                try {
                    executorService.submit(task);
                } catch (Exception e) {
                    log.error("failed to acquire mass task message from pool. WeComMassTaskResultMessage={}",
                            item, e);
                }
            }
            offset = (messages.size() < limitSize) ? 0 : (offset + limitSize);
        } while (offset > 0);
    }

    public class WeComQueryMemberMetrics implements Runnable {
        private long id;
        private String projectId;
        private String taskUuid;
        private String taskType;
        private String sendIdType;
        private String sendId;
        private String corpId;
        private String agentId;

        public WeComQueryMemberMetrics(long id, String projectId, String taskUuid, String taskType, String sendIdType,
                                       String sendId, String corpId, String agentId) {
            this.id = id;
            this.projectId = projectId;
            this.taskUuid = taskUuid;
            this.taskType = taskType;
            this.sendIdType = sendIdType;
            this.sendId = sendId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            log.info("start query mass task for member.taskType={}, sendIdType={}", taskType, sendIdType);
            if ((taskType.equals(WeComMassTaskTypeEnum.SINGLE.name())
                    || taskType.equals(WeComMassTaskTypeEnum.GROUP.name())) && sendIdType.equals(WeComMassTaskSendIdType.MSG_ID.name())) {
                String cursor = "";
                do {
                    log.info("query mass task for member next page.taskType={}, sendIdType={}, cursor={}", taskType
                            , sendIdType, cursor);
                    WeComQueryMemberResultClientResponse resultResponse =
                            queryMassTaskForMemberResult(projectId, taskUuid, taskType, corpId, agentId, sendId,
                                    cursor);
                    if (resultResponse == null) {
                        log.error("failed to query mass task for member result.taskUuid={}, sendId={}", taskUuid,
                                sendId);
                        weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
                        return;
                    }
                    cursor = resultResponse.getNextCursor();
                    for (WeComQueryMemberResultClientResponse.TaskListMessage item : resultResponse.getTaskList()) {
                        if (item.getStatus() == MEMBER_STATUS_SENT) {
                            Runnable task =
                                    new WeComQueryExternalUserMetrics(id, projectId, taskUuid, taskType, sendIdType,
                                            item.getUserId(), sendId, corpId, agentId);
                            try {
                                executorService.submit(task);
                            } catch (Exception e) {
                                log.error("failed to acquire mass task message from pool. " +
                                        "WeComMassTaskResultMessage={}", item, e);
                            }
                        } else {
                            weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(
                                    WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
                        }
                    }
                } while (StringUtils.isNotBlank(cursor));
            } else if (taskType.equals(WeComMassTaskTypeEnum.MOMENT.name()) && sendIdType.equals(WeComMassTaskSendIdType.JOB_ID.name())) {
                queryMomentMassTaskCreateResult(projectId, taskUuid, corpId, agentId, sendId);
                weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus
                                .COMPLETE.name(),
                        id);
            } else if (taskType.equals(WeComMassTaskTypeEnum.MOMENT.name()) && sendIdType.equals(WeComMassTaskSendIdType.MOMENT_ID.name())) {
                String cursor = "";
                do {
                    log.info("query mass task for member publish status next page.taskType={}, sendIdType={}, " +
                            "cursor={}", taskType, sendIdType, cursor);
                    WeComMomentMassTaskPublishResultClientResponse response =
                            queryMomentMassTaskPublishResult(projectId, taskUuid, corpId, agentId, sendId, cursor);
                    if (response == null) {
                        log.error("failed to query mass task for member publish status.");
                        weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(
                                WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
                        return;
                    }
                    cursor = response.getNextCursor();

                    for (WeComMomentMassTaskPublishResultClientResponse.TaskListMessage item : response.getTaskList()) {
                        if (item.getPublishStatus() == MEMBER_STATUS_PUBLISH) {

                            Runnable task =
                                    new WeComQueryExternalUserMetrics(id, projectId, taskUuid, taskType, sendIdType,
                                            item.getUserId(), sendId, corpId, agentId);
                            try {
                                executorService.submit(task);
                            } catch (Exception e) {
                                log.error("failed to acquire mass task message from pool. " +
                                        "WeComMassTaskResultMessage={}", item, e);
                            }
                        } else {
                            weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(
                                    WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
                        }
                    }
                } while (StringUtils.isNotBlank(cursor));
            }
        }
    }

    public class WeComQueryExternalUserMetrics implements Runnable {
        private long id;
        private String projectId;
        private String taskUuid;
        private String taskType;
        private String sendIdType;
        private String sendId;
        private String corpId;
        private String agentId;
        private String memberId;

        public WeComQueryExternalUserMetrics(long id, String projectId, String taskUuid, String taskType,
                                             String sendIdType, String memberId, String sendId, String corpId,
                                             String agentId) {
            this.id = id;
            this.projectId = projectId;
            this.taskUuid = taskUuid;
            this.taskType = taskType;
            this.sendIdType = sendIdType;
            this.memberId = memberId;
            this.sendId = sendId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            log.info("start to query external user.taskType={}, sendIdType={}", taskType, sendIdType);
            if ((taskType.equals(WeComMassTaskTypeEnum.SINGLE.name()) || taskType.equals(WeComMassTaskTypeEnum
                    .GROUP.name())) && sendIdType.equals(WeComMassTaskSendIdType.MSG_ID.name())) {
                String cursor = "";
                do {
                    log.info("query mass task for external user.taskType={}, sendIdType={}, cursor={}", taskType,
                            sendIdType, cursor);
                    WeComQueryExternalUserResultClientResponse resultResponse =
                            querySingleOrGroupMassTaskExternalUserStatus(projectId, taskUuid, taskType, corpId, agentId,
                                    sendId, memberId, cursor);
                    if (resultResponse == null) {
                        log.error("failed to query mass task for external user.taskType={}, sendIdType={}, cursor={}"
                                , taskType, sendIdType, cursor);
                        weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
                        return;
                    }
                    cursor = resultResponse.getNextCursor();

                } while (StringUtils.isNotBlank(cursor));
            } else if (taskType.equals(WeComMassTaskTypeEnum.MOMENT.name()) && sendIdType.equals(WeComMassTaskSendIdType.MOMENT_ID.name())) {
                queryMomentMassTaskComments(projectId, taskUuid, corpId, agentId, sendId, memberId);
            }
            weComMassTaskSyncStatisticRepository.updateWeComMassTaskSyncStatus(WeComMassTaskSyncMetricsStatus.COMPLETE.name(), id);
        }
    }


    public class WeComQueryMomentSendResultMetrics implements Runnable {
        private String projectId;
        private String taskUuid;
        private String momentId;
        private String corpId;
        private String agentId;
        private String memberId;

        public WeComQueryMomentSendResultMetrics(String projectId, String taskUuid, String memberId, String momentId,
                                                 String corpId, String agentId) {
            this.projectId = projectId;
            this.taskUuid = taskUuid;
            this.memberId = memberId;
            this.momentId = momentId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            log.info("start to query mass task from moment send result.momentId={}, taskUuid={}", momentId,
                    taskUuid);
            String cursor = "";
            do {
                log.info("query mass task from moment send result.momentId={}, taskUuid={}, cursor={}", momentId,
                        taskUuid, cursor);
                WeComMomentMassTaskSendResultClientResponse resultResponse =
                        queryMomentMassTaskSendResult(projectId, taskUuid, corpId, agentId, memberId, momentId,
                                cursor);
                if (resultResponse == null) {
                    log.error("failed to query mass task from moment send result.momentId={}, taskUuid={}, cursor={}"
                            , momentId, taskUuid, cursor);
                    return;
                }
                cursor = resultResponse.getNextCursor();

            } while (StringUtils.isNotBlank(cursor));
        }
    }

    /**
     * 查询发送群发任务的员工状态
     *
     * @param projectId
     * @param corpId
     * @param agentId
     * @param msgId
     */

    private WeComQueryMemberResultClientResponse queryMassTaskForMemberResult(String projectId, String taskUuid,
                                                                              String taskType, String corpId,
                                                                              String agentId, String msgId,
                                                                              String cursor) {
        rateLimiter.acquire();
        log.info(
                "start query mass task from single or group. projectUuid={}, taskUuid={}, corpId={}, agentId={}, " +
                        "msgId={}, cursor={}", projectId, taskUuid, corpId, agentId, msgId, cursor);
        WeComQueryMemberResultClientRequest
                weComMemberResultClientRequest = new WeComQueryMemberResultClientRequest();
        weComMemberResultClientRequest.setAgentId(agentId);
        weComMemberResultClientRequest.setCorpId(corpId);
        weComMemberResultClientRequest.setMsgId(msgId);
        weComMemberResultClientRequest.setCursor(cursor);
        RpcResponse<WeComQueryMemberResultClientResponse> response =
                weComMassTaskRpcService.queryMassTaskForMemberResult(weComMemberResultClientRequest);
        log.info("query mass task from single or group result. msgId={}, response={}", msgId, response);
        if (!response.getSuccess()) {
            log.info("failed to query mass task from single or group result. projectUuid={}, taskUuid={}, corpId={}, " +
                    "agentId={}, msgId={},response={}", projectId, taskUuid, corpId, agentId, msgId, response);
            return null;
        }
        WeComQueryMemberResultClientResponse weComQueryMemberResultClientResponse = response.getData();

        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setProjectUuid(projectId);
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setCorpId(corpId);
        weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.GROUP.name().equals(taskType) ?
                WeComMassTaskTypeEnum.GROUP : WeComMassTaskTypeEnum.SINGLE);
        weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL);

        WeComMassTaskMetricsBO.MemberMessage memberMessage = new WeComMassTaskMetricsBO.MemberMessage();
        memberMessage.setMsgId(msgId);
        memberMessage.setFinish(StringUtils.isBlank(weComQueryMemberResultClientResponse.getNextCursor()) ?
                Boolean.TRUE : Boolean.FALSE);

        if (CollectionUtils.isEmpty(weComQueryMemberResultClientResponse.getTaskList())) {
            weComMassTaskSyncStatisticRepository.deleteByTaskUuidAndSendId(taskUuid, msgId);
        } else {
            for (WeComQueryMemberResultClientResponse.TaskListMessage item :
                    weComQueryMemberResultClientResponse.getTaskList()) {
                if (item.getStatus() != MEMBER_STATUS_UNSENT) {
                    List<WeComMassTaskMetricsBO.MemberStatus> memberStatuses = new ArrayList<>();
                    WeComMassTaskMetricsBO.MemberStatus status = new WeComMassTaskMetricsBO.MemberStatus();
                    status.setStatus(getMemberStatus(item.getStatus()));
                    status.setMemberId(item.getUserId());
                    status.setTime(String.valueOf(item.getSendTime()));
                    memberStatuses.add(status);

                    memberMessage.setMemberState(memberStatuses);
                    weComMassTaskMetricsBO.setMemberMessage(memberMessage);
                    log.info("send to mass task from single or group metrics result to queue. MassTaskMetrics={}",
                            weComMassTaskMetricsBO);
                    produceRabbitMqMessage(weComMassTaskMetricsBO);
                }
            }
        }
        log.info("finish to query mass task from single or group metrics");
        return weComQueryMemberResultClientResponse;
    }

    /**
     * 查询群发任务的员工执行状态
     *
     * @param projectId
     * @param corpId
     * @param agentId
     * @param msgId
     * @param memberId
     */
    private WeComQueryExternalUserResultClientResponse querySingleOrGroupMassTaskExternalUserStatus(String projectId,
                                                                                                    String taskUuid,
                                                                                                    String taskType,
                                                                                                    String corpId,
                                                                                                    String agentId,
                                                                                                    String msgId,
                                                                                                    String memberId,
                                                                                                    String cursor) {
        log.info(
                "start query mass task from single or group member send result. projectUuid={}, taskUuid={}, " +
                        "corpId={}, agentId={}, msgId={}, memberId={}, cursor={}",
                projectId, taskUuid, corpId, agentId, msgId, memberId, cursor);
        WeComQueryExternalUserResultClientRequest
                weComMemberSendResultClientRequest = new WeComQueryExternalUserResultClientRequest();
        weComMemberSendResultClientRequest.setAgentId(agentId);
        weComMemberSendResultClientRequest.setCorpId(corpId);
        weComMemberSendResultClientRequest.setMsgid(msgId);
        weComMemberSendResultClientRequest.setUserid(memberId);
        weComMemberSendResultClientRequest.setCursor(cursor);
        RpcResponse<WeComQueryExternalUserResultClientResponse> response =
                weComMassTaskRpcService.queryMassTaskForExternalUserResult
                        (weComMemberSendResultClientRequest);
        log.info("query mass task from single or group member send result from weCom. msgId={}, memberId={}, " +
                "response={}", msgId, memberId, response);
        if (!response.getSuccess()) {
            log.info(
                    "failed to query mass task from single or group result. projectUuid={}, taskUuid={}, corpId={}, " +
                            "agentId={}, msgId={},response={}", projectId, taskUuid, corpId, agentId, msgId, response);
            return null;
        }
        WeComQueryExternalUserResultClientResponse weComGetSingleOrGroupTaskSendResultResponse = response.getData();
        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setProjectUuid(projectId);
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.GROUP.name().equals(taskType) ?
                WeComMassTaskTypeEnum.GROUP : WeComMassTaskTypeEnum.SINGLE);
        weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MASS_TASK_EXTERNAL_USER_DETAIL);

        WeComMassTaskMetricsBO.ExternalUserMessage externalUserMessage =
                new WeComMassTaskMetricsBO.ExternalUserMessage();
        externalUserMessage.setMemberId(memberId);
        externalUserMessage.setMsgId(msgId);
        externalUserMessage.setFinish(StringUtils.isBlank(weComGetSingleOrGroupTaskSendResultResponse.getNextCursor
                ()) ? Boolean.TRUE : Boolean.FALSE);
        List<WeComMassTaskMetricsBO.ExternalUserStatus> externalUserStatusList = new ArrayList<>();
        if (CollectionUtils.isEmpty(weComGetSingleOrGroupTaskSendResultResponse.getSendList())) {
            log.info(
                    "query mass task from single or group member send result is empty. projectUuid={}, taskUuid={}, " +
                            "corpId={}, agentId={}, msgId={}, memberId={}, cursor={}",
                    projectId, taskUuid, corpId, agentId, msgId, memberId, cursor);
        } else {
            for (WeComQueryExternalUserResultClientResponse.SendListMessage item :
                    weComGetSingleOrGroupTaskSendResultResponse.getSendList()) {
                WeComMassTaskMetricsBO.ExternalUserStatus status =
                        new WeComMassTaskMetricsBO.ExternalUserStatus();
                status.setExternalUserId(item.getExternalUserId() == null ? item.getChatId() :
                        item.getExternalUserId());
                status.setStatus(getExternalUserStatus(item.getStatus()));
                status.setTime(String.valueOf(item.getSendTime()));
                externalUserStatusList.add(status);
            }
        }
        externalUserMessage.setExternalUserStatus(externalUserStatusList);
        weComMassTaskMetricsBO.setExternalUserMessage(externalUserMessage);
        log.info("send to mass task from single or group send metrics result to queue. MassTaskMetrics={}",
                weComMassTaskMetricsBO);
        produceRabbitMqMessage(weComMassTaskMetricsBO);
        return weComGetSingleOrGroupTaskSendResultResponse;
    }


    /**
     * 查询群发朋友圈的创建状态
     *
     * @param projectId
     * @param corpId
     * @param agentId
     * @param jobId
     */
    private void queryMomentMassTaskCreateResult(String projectId, String taskUuid, String corpId, String agentId,
                                                 String jobId) {
        log.info(
                "start query mass task from moment create result. projectUuid={}, taskUuid={}, corpId={}, agentId={}," +
                        "jobId={}", projectId, taskUuid, corpId, agentId, jobId);
        WeComMomentMassTaskCreateResultClientRequest
                weComMomentMassTaskCreateResultClientRequest = new WeComMomentMassTaskCreateResultClientRequest();
        weComMomentMassTaskCreateResultClientRequest.setCorpId(corpId);
        weComMomentMassTaskCreateResultClientRequest.setAgentId(agentId);
        weComMomentMassTaskCreateResultClientRequest.setJobid(jobId);
        RpcResponse<WeComMassTaskForMomentCreateResponse> response =
                weComMassTaskRpcService.queryMomentMassTaskForCreateResult(weComMomentMassTaskCreateResultClientRequest);
        log.info("query mass task from moment create result. jobId={}, response={}", jobId, response);
        if (response.getSuccess()) {
            WeComMassTaskForMomentCreateResponse weComMassTaskForMomentCreateResponse = response.getData();
            if (weComMassTaskForMomentCreateResponse.getStatus() == CREATE_MOMENT_STATUE_COMPLETE &&
                    weComMassTaskForMomentCreateResponse.getResult().getCode().equals(ErrorCodeEnum.OK.getCode())) {
                String momentId = weComMassTaskForMomentCreateResponse.getResult().getMomentId();
                weComMassTaskSyncStatisticRepository.updateSendIdMsg(jobId, momentId,
                        WeComMassTaskSendIdType.MOMENT_ID.name());


                if (weComMassTaskForMomentCreateResponse.getResult().getInvalidSenderList() != null) {
                    List<String> invalidUserList =
                            weComMassTaskForMomentCreateResponse.getResult().getInvalidSenderList().getUserList();
                    if (CollectionUtils.isNotEmpty(invalidUserList)) {
                        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
                        weComMassTaskMetricsBO.setProjectUuid(projectId);
                        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
                        weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.MOMENT);
                        weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL);
                        WeComMassTaskMetricsBO.MemberMessage memberMessage = new WeComMassTaskMetricsBO.MemberMessage();
                        List<WeComMassTaskMetricsBO.MemberStatus> memberStatuses = new ArrayList<>();

                        for (String item : invalidUserList) {
                            WeComMassTaskMetricsBO.MemberStatus memberStatus =
                                    new WeComMassTaskMetricsBO.MemberStatus();
                            memberStatus.setStatus(WeComMassTaskMemberStatusEnum.DIMISSION);
                            memberStatus.setMemberId(item);
                            memberStatuses.add(memberStatus);
                        }
                        memberMessage.setFinish(Boolean.FALSE);
                        memberMessage.setMemberState(memberStatuses);
                        weComMassTaskMetricsBO.setMemberMessage(memberMessage);
                        log.info("send mass task from moment create result to queue. MassTaskMetrics={}",
                                weComMassTaskMetricsBO);
                        produceRabbitMqMessage(weComMassTaskMetricsBO);
                    }
                }
            }
        }
    }

    /**
     * 查询群发朋友圈任务的员工发布状态
     */
    private WeComMomentMassTaskPublishResultClientResponse queryMomentMassTaskPublishResult(String projectId,
                                                                                            String taskUuid,
                                                                                            String corpId,
                                                                                            String agentId,
                                                                                            String momentId,
                                                                                            String cursor) {
        log.info(
                "start query mass task from moment publish result. projectUuid={}, taskUuid={}, corpId={}, agentId = " +
                        "{},  momentId={}", projectId, taskUuid, corpId, agentId, momentId);
        WeComMomentMassTaskPublishResultClientRequest
                weComMassTaskForMomentPublishResultRequest = new WeComMomentMassTaskPublishResultClientRequest();
        weComMassTaskForMomentPublishResultRequest.setAgentId(agentId);
        weComMassTaskForMomentPublishResultRequest.setCorpId(corpId);
        weComMassTaskForMomentPublishResultRequest.setMomentId(momentId);
        weComMassTaskForMomentPublishResultRequest.setCursor(cursor);
        weComMassTaskForMomentPublishResultRequest.setLimit(1000);
        RpcResponse<WeComMomentMassTaskPublishResultClientResponse> response =
                weComMassTaskRpcService.queryMomentMassTaskForPublishResult(weComMassTaskForMomentPublishResultRequest);
        log.info("query mass task from moment publish result. momentId={}, response={}", momentId, response);
        if (!response.getSuccess()) {
            log.info(
                    "failed to query mass task from single or group result. projectUuid={}, taskUuid={}, corpId={}, " +
                            "agentId={}, msgId={},response={}", projectId, taskUuid, corpId, agentId, momentId,
                    response);
            return null;
        }
        WeComMomentMassTaskPublishResultClientResponse weComMassTaskForMomentPublishResultResponse = response.getData();

        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setProjectUuid(projectId);
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.MOMENT);
        weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MASS_TASK_MEMBER_DETAIL);

        WeComMassTaskMetricsBO.MemberMessage createMomentMessage =
                new WeComMassTaskMetricsBO.MemberMessage();
        createMomentMessage.setMsgId(momentId);

        for (WeComMomentMassTaskPublishResultClientResponse.TaskListMessage item :
                weComMassTaskForMomentPublishResultResponse.getTaskList()) {
            if (item.getPublishStatus() == MEMBER_STATUS_PUBLISH) {
                List<WeComMassTaskMetricsBO.MemberStatus> memberStatuses = new ArrayList<>();
                WeComMassTaskMetricsBO.MemberStatus status = new WeComMassTaskMetricsBO.MemberStatus();
                status.setStatus(getMemberStatus(item.getPublishStatus()));
                status.setMemberId(item.getUserId());
                memberStatuses.add(status);

                createMomentMessage.setMemberState(memberStatuses);
                weComMassTaskMetricsBO.setMemberMessage(createMomentMessage);
                log.info("send to mass task from moment metrics publish result to queue. MassTaskMetrics={}",
                        weComMassTaskMetricsBO);
                produceRabbitMqMessage(weComMassTaskMetricsBO);

                Runnable task =
                        new WeComQueryMomentSendResultMetrics(projectId, taskUuid, item.getUserId(), momentId, corpId
                                , agentId);
                try {
                    executorService.submit(task);
                } catch (Exception e) {
                    log.error("failed to acquire mass task message from pool. " + "TaskListMessage={}", item, e);
                }
            }
        }

        log.info("finish to query mass task from moment publish result");
        return weComMassTaskForMomentPublishResultResponse;
    }

    /**
     * 查询群发朋友圈任务的员工发布可见客户
     */
    private WeComMomentMassTaskSendResultClientResponse queryMomentMassTaskSendResult(String projectId,
                                                                                      String taskUuid,
                                                                                      String corpId,
                                                                                      String agentId,
                                                                                      String memberId,
                                                                                      String momentId,
                                                                                      String cursor) {
        log.info(
                "start query mass task from moment send result. projectUuid={}, taskUuid={}, corpId={}, agentId = {}," +
                        " memberId={}, momentId={}", projectId, taskUuid, corpId, agentId, memberId, momentId);
        WeComMomentMassTaskSendResultClientRequest
                weComMomentMassTaskSendResultClientRequest = new WeComMomentMassTaskSendResultClientRequest();
        weComMomentMassTaskSendResultClientRequest.setAgentId(agentId);
        weComMomentMassTaskSendResultClientRequest.setCorpId(corpId);
        weComMomentMassTaskSendResultClientRequest.setMomentId(momentId);
        weComMomentMassTaskSendResultClientRequest.setUserId(memberId);
        weComMomentMassTaskSendResultClientRequest.setCursor(cursor);
        RpcResponse<WeComMomentMassTaskSendResultClientResponse> response =
                weComMassTaskRpcService.queryMomentMassTaskForSendResult(weComMomentMassTaskSendResultClientRequest);
        log.info("query mass task from moment send result. momentId={}, response={}", momentId, response);
        if (!response.getSuccess()) {
            log.info(
                    "failed to query mass task from moment send result. projectUuid={}, taskUuid={}, corpId={}, " +
                            "agentId = {},memberId={}, momentId={}, response={}", projectId, taskUuid, corpId, agentId,
                    memberId, momentId, response);
            return null;
        }
        WeComMomentMassTaskSendResultClientResponse weComMomentMassTaskSendResultClientResponse = response.getData();

        WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
        weComMassTaskMetricsBO.setProjectUuid(projectId);
        weComMassTaskMetricsBO.setTaskUuid(taskUuid);
        weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.MOMENT);
        weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MASS_TASK_EXTERNAL_USER_DETAIL);
        weComMassTaskMetricsBO.setCorpId(corpId);
        WeComMassTaskMetricsBO.ExternalUserMessage externalUserMessage =
                new WeComMassTaskMetricsBO.ExternalUserMessage();
        externalUserMessage.setMsgId(momentId);
        externalUserMessage.setMemberId(memberId);
        List<WeComMassTaskMetricsBO.ExternalUserStatus> externalUserStatuses = new ArrayList<>();
        for (WeComMomentMassTaskSendResultClientResponse.CustomerListMessage item :
                weComMomentMassTaskSendResultClientResponse.getCustomerList()) {

            WeComMassTaskMetricsBO.ExternalUserStatus status = new WeComMassTaskMetricsBO.ExternalUserStatus();
            status.setStatus(WeComMassTaskExternalUserStatusEnum.DELIVERED);
            status.setExternalUserId(item.getExternalUserId());
            externalUserStatuses.add(status);
        }
        externalUserMessage.setExternalUserStatus(externalUserStatuses);
        externalUserMessage.setFinish(Boolean.FALSE);
        weComMassTaskMetricsBO.setExternalUserMessage(externalUserMessage);
        log.info("send to mass task from moment metrics send result to queue. MassTaskMetrics={}",
                weComMassTaskMetricsBO);
        produceRabbitMqMessage(weComMassTaskMetricsBO);
        log.info("finish to query mass task from moment metrics send result.");
        return weComMomentMassTaskSendResultClientResponse;
    }

    /**
     * 查询群发朋友圈后的评论状态
     *
     * @param projectId
     * @param corpId
     * @param agentId
     * @param momentId
     * @param memberId
     */
    private void queryMomentMassTaskComments(String projectId, String taskUuid, String corpId, String agentId,
                                             String momentId, String memberId) {
        log.info(
                "start query mass task from moment comments result. projectUuid={}, taskUuid={}, corpId={}, " +
                        "agentId={},momentId={}, memberId={}", projectId, taskUuid, corpId, agentId, momentId,
                memberId);
        WeComMomentMassTaskCommentsClientRequest
                weComMassTaskForMomentCommentsRequest = new WeComMomentMassTaskCommentsClientRequest();
        weComMassTaskForMomentCommentsRequest.setCorpId(corpId);
        weComMassTaskForMomentCommentsRequest.setAgentId(agentId);
        weComMassTaskForMomentCommentsRequest.setMomentId(momentId);
        weComMassTaskForMomentCommentsRequest.setUserId(memberId);
        RpcResponse<WeComMomentMassTaskCommentsClientResponse> response =
                weComMassTaskRpcService.queryMomentMassTaskForCommentsResult(weComMassTaskForMomentCommentsRequest);
        log.info("query mass task from moment comments result. momentId={}, memberId={}, response={}", momentId,
                memberId, response);
        if (response.getSuccess()) {
            WeComMomentMassTaskCommentsClientResponse weComMassTaskForMomentCommentsResponse = response.getData();

            WeComMassTaskMetricsBO weComMassTaskMetricsBO = new WeComMassTaskMetricsBO();
            weComMassTaskMetricsBO.setProjectUuid(projectId);
            weComMassTaskMetricsBO.setTaskUuid(taskUuid);
            weComMassTaskMetricsBO.setCorpId(corpId);
            weComMassTaskMetricsBO.setTaskType(WeComMassTaskTypeEnum.MOMENT);
            weComMassTaskMetricsBO.setMetricType(WeComMassTaskMetricTypeEnum.MOMENT_TASK_COMMENTS_DETAIL);

            WeComMassTaskMetricsBO.MomentCommentsMessage momentCommentsMessage =
                    new WeComMassTaskMetricsBO.MomentCommentsMessage();
            momentCommentsMessage.setMomentId(momentId);
            momentCommentsMessage.setMemberId(memberId);

            List<WeComMassTaskMetricsBO.ExternalUserStatus> externalUserStatus = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(weComMassTaskForMomentCommentsResponse.getCommentList())) {
                for (WeComMomentMassTaskCommentsClientResponse.CommentMessage item :
                        weComMassTaskForMomentCommentsResponse.getCommentList()) {
                    WeComMassTaskMetricsBO.ExternalUserStatus status =
                            new WeComMassTaskMetricsBO.ExternalUserStatus();
                    if (StringUtils.isNotBlank(item.getExternalUserId())) {
                        status.setExternalUserId(item.getExternalUserId());
                        status.setType(WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue());

                    } else {
                        status.setExternalUserId(item.getUserId());
                        status.setType(WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue());
                    }
                    status.setStatus(WeComMassTaskExternalUserStatusEnum.COMMENT);
                    status.setTime(String.valueOf(item.getCreateTime() * 1000));
                    externalUserStatus.add(status);
                }
            }

            if (CollectionUtils.isNotEmpty(weComMassTaskForMomentCommentsResponse.getLikeList())) {
                for (WeComMomentMassTaskCommentsClientResponse.CommentMessage item :
                        weComMassTaskForMomentCommentsResponse.getLikeList()) {
                    WeComMassTaskMetricsBO.ExternalUserStatus status =
                            new WeComMassTaskMetricsBO.ExternalUserStatus();
                    if (StringUtils.isNotBlank(item.getExternalUserId())) {
                        status.setExternalUserId(item.getExternalUserId());
                        status.setType(WeComMassTaskMetricsType.MASS_TASK_EXTERNAL_USER.getValue());

                    } else {
                        status.setExternalUserId(item.getUserId());
                        status.setType(WeComMassTaskMetricsType.MASS_TASK_MEMBER.getValue());
                    }
                    status.setStatus(WeComMassTaskExternalUserStatusEnum.LIKE);
                    status.setTime(String.valueOf(item.getCreateTime() * 1000));
                    externalUserStatus.add(status);
                }
            }
            if (CollectionUtils.isNotEmpty(externalUserStatus)) {
                momentCommentsMessage.setExternalUserStatus(externalUserStatus);
                weComMassTaskMetricsBO.setMomentCommentsMessage(momentCommentsMessage);
                log.info("send to mass task from moment comments result to queue. MassTaskMetrics={}",
                        weComMassTaskMetricsBO);
                produceRabbitMqMessage(weComMassTaskMetricsBO);
            }
        }
    }

    private WeComMassTaskMemberStatusEnum getMemberStatus(int status) {
        if (status == MEMBER_STATUS_UNSENT) {
            return WeComMassTaskMemberStatusEnum.UNSENT;
        } else if (status == MEMBER_STATUS_SENT || status == MEMBER_STATUS_PUBLISH) {
            return WeComMassTaskMemberStatusEnum.SENT;
        }
        return WeComMassTaskMemberStatusEnum.UNSENT;
    }

    private WeComMassTaskExternalUserStatusEnum getExternalUserStatus(int status) {
        if (status == EXTERNAL_USER_STATUS_UNDELIVERED) {
            return WeComMassTaskExternalUserStatusEnum.UNDELIVERED;
        } else if (status == EXTERNAL_USER_STATUS_DELIVERED) {
            return WeComMassTaskExternalUserStatusEnum.DELIVERED;
        } else if (status == EXTERNAL_USER_STATUS_UNFRIEND) {
            return WeComMassTaskExternalUserStatusEnum.UNFRIEND;
        } else if (status == EXTERNAL_USER_STATUS_EXCEED_LIMIT) {
            return WeComMassTaskExternalUserStatusEnum.EXCEED_LIMIT;
        }
        return WeComMassTaskExternalUserStatusEnum.UNDELIVERED;
    }

    private void produceRabbitMqMessage(Object values) {
        weComMassTaskStatisticAmqpTemplate.convertAndSend(RabbitMqConstants.EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_STATISTIC, RabbitMqConstants.ROUTING_KEY_WECOM_MASS_TASK_STATISTIC, values);
    }

    @Data
    public static class WeComCorpMessage {
        private String corpId;
        private String agentId;
    }

    public enum WeComMassTaskSyncMetricsStatus {
        INIT("INIT"),
        RUNNING("RUNNING"),
        COMPLETE("COMPLETE");
        private final String syncStatus;

        WeComMassTaskSyncMetricsStatus(String syncStatus) {
            this.syncStatus = syncStatus;
        }
    }
}
