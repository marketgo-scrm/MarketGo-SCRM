package com.easy.marketgo.biz.service.cdp;

import cn.hutool.crypto.SecureUtil;
import com.easy.marketgo.core.model.cdp.CrowdUsersBaseRequest;
import com.easy.marketgo.core.service.cdp.CdpManagerService;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.cdp.CdpSyncCrowdUsersStatusEnum;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.cdp.CdpConfigEntity;
import com.easy.marketgo.core.entity.cdp.CdpCrowdUsersSyncEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.repository.cdp.CdpConfigRepository;
import com.easy.marketgo.core.repository.cdp.CdpCrowdUsersSyncRepository;
import com.easy.marketgo.core.repository.usergroup.UserGroupCdpRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/29/22 10:48 PM
 * Describe:
 */
@Slf4j
@Service
public class CdpCrowdUserSyncService {

    @Autowired
    private CdpCrowdUsersSyncRepository cdpCrowdUsersSyncRepository;

    @Autowired
    private CdpConfigRepository cdpConfigRepository;

    @Autowired
    private CdpManagerService cdpManagerService;

    @Autowired
    private UserGroupCdpRepository userGroupCdpRepository;

    @Autowired
    private WeComMassTaskRepository weComMassTaskRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    private ExecutorService executorService;

    private RateLimiter rateLimiter = RateLimiter.create(2);

    private static final Integer CAPACITY_ONE = 100;
    private static final Integer CORE_THREAD_NUM = 2;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("query_crowd_users_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    public void computeCrowdUsers() {
        log.info("start to compute crowd users task");
        List<String> taskUuidList =
                cdpCrowdUsersSyncRepository.queryTaskUuidByCrowdCode(CdpSyncCrowdUsersStatusEnum.SYNC_COMPLETE.getValue());
        if (CollectionUtils.isEmpty(taskUuidList)) {
            return;
        }
        log.info("compute crowd users task. taskUuidList={}", taskUuidList);
        for (String item : taskUuidList) {
            List<CdpCrowdUsersSyncEntity> entities =
                    cdpCrowdUsersSyncRepository.getCrowdsByTaskUuidAndSyncStatus(item,
                            CdpSyncCrowdUsersStatusEnum.SYNC_COMPLETE.getValue());
            if (CollectionUtils.isNotEmpty(entities)) {
                continue;
            }

            List<String> crowdCodes =
                    cdpCrowdUsersSyncRepository.queryCrowdCodeByTaskUuidAndSyncStatus(item,
                            CdpSyncCrowdUsersStatusEnum.SYNC_COMPLETE.getValue());
            if (CollectionUtils.isNotEmpty(crowdCodes)) {

                Runnable task =
                        new InnerQueryCdpCrowdUsers(item, crowdCodes);
                try {
                    executorService.submit(task);
                } catch (Exception e) {
                    log.error("failed to acquire compute crowd users from pool. taskUuid={}", item, e);
                }
            }
        }
    }

    public void syncCrowdUsers() {
        log.info("start to query sync crowd users task");
        List<CdpCrowdUsersSyncEntity> entities =
                cdpCrowdUsersSyncRepository.getQueryCrowdUsersBySyncStatus(CdpSyncCrowdUsersStatusEnum.UNSTART.getValue());
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        log.info("query sync crowd users task. entities={}", entities);
        for (CdpCrowdUsersSyncEntity entity : entities) {

            CdpConfigEntity cdpConfigEntity =
                    cdpConfigRepository.getCdpConfigByCorpIdAndCdpType(entity.getProjectUuid(), entity.getCorpId(),
                            entity.getCdpType());
            if (cdpConfigEntity != null) {
                String cdpUuid = cdpConfigEntity.getUuid();
                CdpCrowdUsersSyncEntity syncEntity =
                        cdpCrowdUsersSyncRepository.getFinishCrowdByCdpAndCrowd(entity.getProjectUuid(),
                                entity.getCorpId(), entity.getCdpType(),
                                Arrays.asList(CdpSyncCrowdUsersStatusEnum.SYNC_COMPLETE.getValue(),
                                        CdpSyncCrowdUsersStatusEnum.FINISHED.getValue()),
                                entity.getCrowdCode());
                if (syncEntity != null) {
                    cdpCrowdUsersSyncRepository.updateSyncStatusByCrowd(entity.getProjectUuid(),
                            entity.getCorpId(), entity.getCdpType(), entity.getCrowdCode(),
                            CdpSyncCrowdUsersStatusEnum.SYNC_COMPLETE.getValue());
                } else {
                    cdpCrowdUsersSyncRepository.updateSyncStatusByCrowd(entity.getProjectUuid(),
                            entity.getCorpId(), entity.getCdpType(), entity.getCrowdCode(),
                            CdpSyncCrowdUsersStatusEnum.SYNCING.getValue());
                    Runnable task =
                            new QueryCdpCrowdUsers(entity.getTaskUuid(), cdpUuid, entity.getCdpType(),
                                    entity.getCrowdCode(), entity.getProjectName(), entity.getCorpId());

                    try {
                        executorService.submit(task);
                    } catch (Exception e) {
                        log.error("failed to acquire query crowd users from pool. entity={}", entity, e);
                    }
                }
            }
        }
    }

    public class InnerQueryCdpCrowdUsers implements Runnable {
        private String taskUuid;
        private List<String> crowdCodes = new ArrayList<>();


        public InnerQueryCdpCrowdUsers(String taskUuid, List<String> crowdCodes) {
            this.taskUuid = taskUuid;
            this.crowdCodes.addAll(crowdCodes);
        }

        @Override
        public void run() {
            log.info("start compute crowd users.taskUuid={}, crowdCode={}", taskUuid, crowdCodes);

            try {
                List<CdpCrowdUsersSyncEntity> entities =
                        cdpCrowdUsersSyncRepository.getCrowdsByTaskUuid(taskUuid);
                if (CollectionUtils.isEmpty(entities)) {
                    return;
                }
                CdpCrowdUsersSyncEntity entity = entities.get(0);
                CdpConfigEntity cdpConfigEntity =
                        cdpConfigRepository.getCdpConfigByCorpIdAndCdpType(entity.getProjectUuid(), entity.getCorpId(),
                                entity.getCdpType());
                List<String> memberIds = userGroupCdpRepository.queryMemberByCorpIdAndCdp(entity.getCorpId(),
                        cdpConfigEntity.getUuid(), crowdCodes, entity.getCdpType());
                if (CollectionUtils.isEmpty(memberIds)) {
                    log.info("compute cdp user group memberId is empty. taskUuid={}, cdpUuid={}, crowdCode={}",
                            taskUuid, cdpConfigEntity.getUuid(),
                            crowdCodes);
                    weComMassTaskRepository.updateTaskStatusByUUID(taskUuid,
                            WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                    return;
                }
                log.info("compute cdp user group memberId count={}, corpId={}, taskUuid={}, crowdCode={}",
                        memberIds.size(), entity.getCorpId(), taskUuid, crowdCodes);
                for (String memberId : memberIds) {
                    List<String> externalUsers =
                            userGroupCdpRepository.queryExternalUsersByMemberId(entity.getCorpId(), memberId
                                    , cdpConfigEntity.getUuid(), crowdCodes, entity.getCdpType());
                    if (CollectionUtils.isEmpty(externalUsers)) {
                        log.info("compute cdp user group externalUser is empty. corpId={}, taskUuid={}, memberId={}, " +
                                "crowdCode={}", entity.getCorpId(), taskUuid, memberId, crowdCodes);
                        weComMassTaskRepository.updateTaskStatusByUUID(taskUuid,
                                WeComMassTaskStatus.COMPUTE_FAILED.getValue());
                        return;
                    }
                    log.info("compute cdp user group externalUser count={}, corpId={}, taskUuid={}, memberId={}",
                            externalUsers.size(), entity.getCorpId(), taskUuid, memberId);

                    WeComMassTaskSendQueueEntity sendQueueEntity = new WeComMassTaskSendQueueEntity();
                    sendQueueEntity.setMemberId(memberId);
                    sendQueueEntity.setUuid(UuidUtils.generateUuid());
                    sendQueueEntity.setMemberMd5(SecureUtil.md5(memberId));
                    sendQueueEntity.setTaskUuid(taskUuid);
                    sendQueueEntity.setExternalUserIds(externalUsers.stream().collect(Collectors.joining(",")));
                    sendQueueEntity.setStatus(WeComMassTaskSendStatusEnum.UNSEND.name());
                    log.info("save mass task send queue from cdp user group. weComMassTaskSendQueueEntity={}",
                            sendQueueEntity);
                    weComMassTaskSendQueueRepository.save(sendQueueEntity);
                }
            } catch (Exception e) {
                log.error("failed to save send offline user group. error={}", e);
            }
        }
    }

    public class QueryCdpCrowdUsers implements Runnable {
        private String taskUuid;
        private String cdpUuid;
        private String corpId;
        private String cdpType;
        private String crowdCode;
        private String projectName;
        private String projectUuid;

        public QueryCdpCrowdUsers(String taskUuid, String cdpUuid, String cdpType, String crowdCode,
                                  String projectName, String corpId) {
            this.taskUuid = taskUuid;
            this.cdpUuid = cdpUuid;
            this.cdpType = cdpType;
            this.crowdCode = crowdCode;
            this.projectName = projectName;
            this.corpId = corpId;
        }

        @Override
        public void run() {
            log.info("start query crowd users from cdp service.taskUuid={}, cdpUuid={}, crowdCode={}", taskUuid,
                    cdpUuid, crowdCode);
            CrowdUsersBaseRequest request = new CrowdUsersBaseRequest();
            cdpManagerService.queryCrowdUsers(request);

            cdpCrowdUsersSyncRepository.updateSyncStatusByCrowd(projectUuid,
                    corpId, cdpType, crowdCode, CdpSyncCrowdUsersStatusEnum.SYNCING.getValue());
        }
    }
}
