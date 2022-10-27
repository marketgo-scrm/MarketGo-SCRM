package com.easy.marketgo.biz.service.wecom.customer;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.customer.QueryMembersClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryDepartmentsRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUserDetailClientRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUsersForMemberClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComQueryExternalUsersForMemberClientResponse;
import com.easy.marketgo.api.model.response.customer.*;
import com.easy.marketgo.api.service.WeComExternalUserRpcService;
import com.easy.marketgo.api.service.WeComMemberRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComRelationType;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 9:50 PM
 * Describe:
 */
@Slf4j
@Service
public class SyncContactsService {

    @Resource
    private WeComMemberRpcService weComMemberRpcService;

    @Resource
    private WeComExternalUserRpcService weComExternalUserRpcService;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    private ExecutorService executorService;

    private RateLimiter rateLimiter = RateLimiter.create(2);

    private static final Integer CAPACITY_ONE = 100;
    private static final Integer CORE_THREAD_NUM = 10;

    private final String AGENT_ID = "externaluser";

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("wecom_sync_contacts_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void getDepartmentList(String projectUuid, String corpId) {
        try {
            WeComAgentMessageEntity agentMessageEntity =
                    weComAgentMessageRepository.getWeComAgentByCorp(projectUuid, corpId);
            log.info("query agent message for mass task. agentMessageEntity={}", agentMessageEntity);
            String agentId = (agentMessageEntity == null ? "" : agentMessageEntity.getAgentId());

            weComMemberMessageRepository.deleteByCorpId(corpId);
            WeComQueryDepartmentsRequest weComQueryDepartmentsRequest = new WeComQueryDepartmentsRequest();
            weComQueryDepartmentsRequest.setCorpId(corpId);
            weComQueryDepartmentsRequest.setAgentId(agentId);
            weComQueryDepartmentsRequest.setDepartmentId("");
            RpcResponse<WeComQueryDepartmentsClientResponse> rpcResponse =
                    weComMemberRpcService.queryDepartments(weComQueryDepartmentsRequest);
            log.info("get departments response. response={}", rpcResponse);
            WeComQueryDepartmentsClientResponse data = rpcResponse.getData();
            if (data == null) {
                log.info("response WeComQueryDepartmentsClientResponse is empty.");
                return;
            }
            List<WeComQueryDepartmentsClientResponse.WeComDepartmentMessage> departmentMessage = data.getDepartment();
            if (CollectionUtils.isEmpty(departmentMessage)) {
                log.info("response department list is empty");
                return;
            }
            saveDepartmentMessage(corpId, agentId, departmentMessage);
            for (WeComQueryDepartmentsClientResponse.WeComDepartmentMessage item : departmentMessage) {
                getDepartmentMemberList(corpId, agentId, String.valueOf(item.getId()));
            }
        } catch (Exception e) {
            log.error("failed to save weCom sync contacts. ", e);
        }
    }

    private void queryMembers(String corpId, String agentId) {
        String cursor = "";
        do {
            log.info("query members next page.corpId={}, agentId={}, cursor={}", corpId, agentId, cursor);

            QueryMembersClientRequest queryMembersClientRequest = new QueryMembersClientRequest();
            queryMembersClientRequest.setCorpId(corpId);
            queryMembersClientRequest.setAgentId(agentId);
            queryMembersClientRequest.setCursor(cursor);
            queryMembersClientRequest.setLimit(10000);
            RpcResponse<QueryMembersClientResponse> rpcResponse =
                    weComMemberRpcService.queryMembers(queryMembersClientRequest);
            if (rpcResponse == null || rpcResponse.getData() == null) {
                log.error("failed to query members next page result.corpId={}, agentId={}, cursor={}", corpId,
                        agentId, cursor);
                return;
            }
            cursor = rpcResponse.getData().getNextCursor();
            for (QueryMembersClientResponse.DepartmentMember item : rpcResponse.getData().getDeptUser()) {
            }
        } while (org.apache.commons.lang3.StringUtils.isNotBlank(cursor));
    }

    private void saveDepartmentMessage(String corpId, String agentId,
                                       List<WeComQueryDepartmentsClientResponse.WeComDepartmentMessage> departments) {
        weComDepartmentRepository.deleteByCorpId(corpId);
        List<WeComDepartmentEntity> departmentList = new ArrayList<>();
        for (WeComQueryDepartmentsClientResponse.WeComDepartmentMessage item : departments) {
            WeComQueryDepartmentsRequest weComQueryDepartmentsRequest = new WeComQueryDepartmentsRequest();
            weComQueryDepartmentsRequest.setCorpId(corpId);
            weComQueryDepartmentsRequest.setAgentId(agentId);
            weComQueryDepartmentsRequest.setDepartmentId(String.valueOf(item.getId()));
            RpcResponse<QueryDepartmentDetailClientResponse> rpcResponse =
                    weComMemberRpcService.queryDepartmentDetail(weComQueryDepartmentsRequest);
            log.info("get department detail response. response={}", rpcResponse);
            QueryDepartmentDetailClientResponse response = rpcResponse.getData();
            if (response != null && response.getDepartment() != null) {
                WeComDepartmentEntity entity = new WeComDepartmentEntity();
                entity.setCorpId(corpId);
                entity.setDepartmentId(item.getId());
                entity.setDepartmentName(response.getDepartment().getName());
                entity.setDepartmentNameEn(response.getDepartment().getNameEn());
                entity.setOrder(item.getOrder());
                entity.setParentId(item.getParentId());
                entity.setDepartmentLeader(response.getDepartment().getDepartmentLeader().stream().collect(Collectors.joining(",")));
                departmentList.add(entity);
            }
        }

        weComDepartmentRepository.saveAll(departmentList);
    }

    private void getDepartmentMemberList(String corpId, String agentId, String departmentId) {
        WeComQueryDepartmentsRequest weComQueryDepartmentsRequest = new WeComQueryDepartmentsRequest();
        weComQueryDepartmentsRequest.setCorpId(corpId);
        weComQueryDepartmentsRequest.setAgentId(agentId);
        weComQueryDepartmentsRequest.setDepartmentId(departmentId);
        RpcResponse<WeComQueryDepartmentMembersClientResponse> rpcResponse =
                weComMemberRpcService.queryDepartmentMembers(weComQueryDepartmentsRequest);
        log.info("get department member list response. response={}", rpcResponse);
        WeComQueryDepartmentMembersClientResponse data = rpcResponse.getData();
        if (data == null) {
            log.info("response WeComQueryDepartmentMembersClientResponse is empty.");
            return;
        }
        List<WeComQueryDepartmentMembersClientResponse.WeComMemberMessage> members = data.getUserList();
        if (CollectionUtils.isEmpty(members)) {
            log.info("response department member list is empty");
            return;
        }

        for (WeComQueryDepartmentMembersClientResponse.WeComMemberMessage item : members) {
            log.info("get member message. corpId={}, memberId={}", corpId, item.getUserId());
            WeComMemberMessageEntity memberMessageEntity =
                    weComMemberMessageRepository.getMemberMessgeByMemberId(corpId, item.getUserId());
            if (memberMessageEntity == null) {
                WeComMemberMessageEntity entity = new WeComMemberMessageEntity();
                entity.setCorpId(corpId);
                entity.setMemberName(item.getName());
                entity.setMemberId(item.getUserId());
                if (StringUtils.isNotEmpty(item.getAvatar())) {
                    entity.setAvatar(item.getAvatar());
                }
                if (StringUtils.isNotEmpty(item.getAlias())) {
                    entity.setAlias(item.getAlias());
                }
                if (StringUtils.isNotEmpty(item.getThumbAvatar())) {
                    entity.setThumbAvatar(item.getThumbAvatar());
                }
                if (StringUtils.isNotEmpty(item.getQrCode())) {
                    entity.setQrCode(item.getQrCode());
                }
                if (StringUtils.isNotEmpty(item.getMobile())) {
                    entity.setMobile(item.getMobile());
                }
                entity.setStatus(item.getStatus());
                entity.setMainDepartment(String.valueOf(item.getMainDepartment()));
                entity.setDepartment(item.getDepartment().stream().map(String::valueOf).collect(Collectors.joining(","
                )));
                log.info("save member info. entity={}", entity);
                weComMemberMessageRepository.save(entity);
            } else {
                log.info("update member info. member={}, memberName={}", item.getUserId(), item
                        .getName());
                weComMemberMessageRepository.updateInfoByMemberId(corpId, item.getUserId(), item
                        .getName(), item.getAvatar(), item.getAlias(), item.getStatus());
            }
            Runnable task =
                    new WeComQueryExternalUsers(corpId, AGENT_ID, item.getUserId());
            try {
                executorService.submit(task);
            } catch (Exception e) {
                log.error("failed to acquire weCom sync contacts from pool. WeComMemberMessage={}",
                        item, e);
            }
        }
    }

    public class WeComQueryExternalUsers implements Runnable {
        private String memberId;
        private String corpId;
        private String agentId;

        public WeComQueryExternalUsers(String corpId, String agentId, String memberId) {
            this.memberId = memberId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            getExternalUsers(corpId, agentId, memberId);
        }

        private void getExternalUsers(final String corpId, final String agentId, final String memberId) {
            WeComQueryExternalUsersForMemberClientRequest request = new WeComQueryExternalUsersForMemberClientRequest();
            request.setMemberId(memberId);
            request.setAgentId(agentId);
            request.setCorpId(corpId);
            log.info("get member external user list request. request={}", request);
            RpcResponse<WeComQueryExternalUsersForMemberClientResponse> rpcResponse =
                    weComExternalUserRpcService.queryExternalUsersForMember(request);
            if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData() == null) {
                log.info("response WeComQueryExternalUsersForMemberClientResponse is empty.");
                return;
            }
            log.info("get member external user list response. response={}", rpcResponse);
            WeComQueryExternalUsersForMemberClientResponse data = rpcResponse.getData();
            if (CollectionUtils.isEmpty(data.getExternalUserId())) {
                log.info("response external user list is empty.");
                return;
            }

            for (String externalUserId : data.getExternalUserId()) {
                syncExternalUserDetail(corpId, externalUserId);
            }
        }
    }


    public void syncExternalUserDetail(String corpId, String externalUserId) {
        Runnable task =
                new WeComQueryExternalUserDetail(corpId, AGENT_ID, externalUserId);
        try {
            executorService.submit(task);
        } catch (Exception e) {
            log.error("failed to acquire weCom sync contacts external user detail from pool. " +
                    "WeComMemberMessage={}", externalUserId, e);
        }
    }

    public class WeComQueryExternalUserDetail implements Runnable {
        private String externalUserId;
        private String corpId;
        private String agentId;

        public WeComQueryExternalUserDetail(String corpId, String agentId, String externalUserId) {
            this.externalUserId = externalUserId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            getExternalUserDetail(corpId, agentId, externalUserId);
        }

        private void getExternalUserDetail(final String corpId, final String agentId, final String externalUserId) {
            try {
                WeComQueryExternalUserDetailClientRequest request = new WeComQueryExternalUserDetailClientRequest();
                request.setExternalUserId(externalUserId);
                request.setCorpId(corpId);
                request.setAgentId(agentId);
                log.info("get member external user detail request. request={}", request);
                RpcResponse<WeComQueryExternalUserDetailClientResponse> rpcResponse =
                        weComExternalUserRpcService.queryExternalUserDetail(request);
                if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData() == null) {
                    log.info("response WeComQueryExternalUserDetailClientResponse is empty.");
                    return;
                }
                log.info("get member external user detail response. response={}", rpcResponse);
                WeComQueryExternalUserDetailClientResponse data = rpcResponse.getData();

                weComRelationMemberExternalUserRepository.updateRelationByExternalUser(corpId, externalUserId);
                if (CollectionUtils.isEmpty(data.getFollowUser())) {
                    log.info("response external user list is empty.");
                    return;
                }

                for (WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUser item : data.getFollowUser()) {
                    WeComRelationMemberExternalUserEntity relation =
                            weComRelationMemberExternalUserRepository.queryByMemberAndExternalUser(corpId,
                                    item.getUserId(), externalUserId);
                    if (relation == null) {
                        relation = new WeComRelationMemberExternalUserEntity();
                    }
                    relation.setCorpId(corpId);
                    relation.setExternalUserId(externalUserId);
                    relation.setMemberId(item.getUserId());
                    relation.setRelationType(WeComRelationType.FRIEND.ordinal());
                    relation.setAddTime(DateUtil.date(item.getCreateTime() * 1000));
                    if (CollectionUtils.isNotEmpty(item.getTags())) {
                        List<WeComQueryExternalUserDetailClientResponse.ExternalUserFollowUserTag> tags =
                                item.getTags();
                        List<String> tagIdList = new ArrayList<>();
                        tags.forEach(tag -> {
                            tagIdList.add(tag.getTagId());
                        });
                        relation.setTags(tagIdList.stream().collect(Collectors.joining(",")));
                    }

                    relation.setRemark(StringUtils.isEmpty(item.getRemark()) ? "" : item.getRemark());
                    relation.setDescription(StringUtils.isEmpty(item.getDescription()) ? "" :
                            item.getDescription());
                    relation.setAddSourceUserId(StringUtils.isEmpty(item.getOperUserId()) ? "" :
                            item.getOperUserId());
                    relation.setExternalUserName(data.getExternalContact().getName());
                    relation.setAvatar(data.getExternalContact().getAvatar());
                    relation.setGender(data.getExternalContact().getGender());
                    relation.setType(data.getExternalContact().getType());
                    relation.setUnionid(data.getExternalContact().getUnionid());
                    relation.setCorpName(data.getExternalContact().getCorpName());
                    relation.setCorpFullName(data.getExternalContact().getCorpFullName());
                    relation.setAddWay(item.getAddWay());
                    weComRelationMemberExternalUserRepository.save(relation);
                }
            } catch (Exception e) {
                log.error("failed to save weCom sync contacts for external users. ", e);
            }
        }
    }
}
