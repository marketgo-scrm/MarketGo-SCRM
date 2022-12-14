package com.easy.marketgo.web.service.wecom.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.service.CdpManagerService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.cdp.CdpConfigEntity;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.entity.usergroup.UserGroupOfflineEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.bo.UserGroupEstimateResultBO;
import com.easy.marketgo.core.repository.cdp.CdpConfigRepository;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.web.model.bo.CdpUserGroupRule;
import com.easy.marketgo.web.model.bo.OfflineUserGroupMessage;
import com.easy.marketgo.web.model.bo.OfflineUserGroupRule;
import com.easy.marketgo.web.model.bo.WeComUserGroupRule;
import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.web.model.response.BaseResponse;
import com.easy.marketgo.web.model.response.UserGroupEstimateResponse;
import com.easy.marketgo.web.model.response.UserGroupMessageResponse;
import com.easy.marketgo.web.model.response.cdp.CdpCrowdListResponse;
import com.easy.marketgo.web.service.wecom.WeComUserGroupService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:29 PM
 * Describe:
 */
@Slf4j
@Service
public class WeComUserGroupServiceImpl implements WeComUserGroupService {

    @Autowired
    private CdpConfigRepository cdpConfigRepository;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private UserGroupOfflineRepository userGroupOfflineRepository;

    @Autowired
    private CdpManagerService cdpManagerService;

    private ExecutorService executorService;

    private static final Integer CAPACITY_ONE = 10;
    private static final Integer CORE_THREAD_NUM = 1;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("wecom_user_group_estimate_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    @Override
    public BaseResponse estimate(String projectId, UserGroupAudienceRules audienceRules, String requestId,
                                 String corpId, String taskType) {

        if (audienceRules.getUserGroupType().equals(UserGroupAudienceTypeEnum.WECOM_USER_GROUP.getValue()) &&
                audienceRules.getWeComUserGroupRule().getMembers() == null) {
            log.error("member message is empty for user group estimate.");
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        if (audienceRules.getUserGroupType().equals(UserGroupAudienceTypeEnum.OFFLIEN_USER_GROUP.getValue()) &&
                (audienceRules.getOfflineUserGroupRule() == null || StringUtils.isBlank(audienceRules.getOfflineUserGroupRule().getUserGroupUuid()))) {
            log.error("offline message is empty for user group estimate.");
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        if (taskType.equals(WeComMassTaskTypeEnum.SINGLE.name()) && audienceRules.getWeComUserGroupRule().getExternalUsers() == null) {
            log.error("external user message is empty for user group estimate.");
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_MASS_TASK_USER_GROUP_EXTERNAL_USER_IS_EMPTY);
        }
        UserGroupEstimateResponse userGroupEstimateResponse = new UserGroupEstimateResponse();
        WeComUserGroupAudienceEntity entity =
                weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByRequestId(projectId, requestId);
        if (entity == null) {
            WeComUserGroupAudienceEntity weComUserGroupAudienceEntity = new WeComUserGroupAudienceEntity();
            weComUserGroupAudienceEntity.setProjectUuid(projectId);
            weComUserGroupAudienceEntity.setUuid(UuidUtils.generateUuid());
            weComUserGroupAudienceEntity.setRequestId(requestId);
            weComUserGroupAudienceEntity.setTaskType(taskType);
            weComUserGroupAudienceEntity.setUserGroupType(audienceRules.getUserGroupType());
            if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.WECOM_USER_GROUP.getValue())) {
                weComUserGroupAudienceEntity.setWecomConditions(JsonUtils.toJSONString(audienceRules.getWeComUserGroupRule()));
            } else if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLIEN_USER_GROUP.getValue())) {
                weComUserGroupAudienceEntity.setOfflineConditions(JsonUtils.toJSONString(audienceRules.getOfflineUserGroupRule()));
            } else if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
                weComUserGroupAudienceEntity.setCdpConditions(JsonUtils.toJSONString(audienceRules.getCdpUserGroupRule()));
            }
            weComUserGroupAudienceEntity.setConditionsRelation(audienceRules.getRelation());
            weComUserGroupAudienceEntity.setStatus(UserGroupAudienceStatusEnum.COMPUTING.getValue());
            weComUserGroupAudienceRepository.save(weComUserGroupAudienceEntity);
            Runnable task = null;
            if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLIEN_USER_GROUP.getValue())) {
                task =
                        new OfflineUserGroupEstimate(projectId, corpId, taskType, requestId,
                                audienceRules.getOfflineUserGroupRule());
            } else if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
                task =
                        new CdpUserGroupEstimate(projectId, corpId, taskType, requestId,
                                audienceRules.getCdpUserGroupRule());
            } else {
                task =
                        new WeComUserGroupEstimate(projectId, corpId, taskType, requestId,
                                audienceRules.getWeComUserGroupRule());
            }
            try {
                executorService.submit(task);
            } catch (Exception e) {
                log.error("failed to acquire weCom user group estimate from pool. requestId={}", requestId, e);
            }
            entity = weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByRequestId(projectId,
                    requestId);
        }

        BeanUtils.copyProperties(entity, userGroupEstimateResponse);
        String result = entity.getEstimateResult();
        if (StringUtils.isNotBlank(result)) {
            UserGroupEstimateResultBO userGroupEstimateResultBO = JsonUtils.toObject(result,
                    UserGroupEstimateResultBO.class);
            userGroupEstimateResponse.setExternalUserCount(userGroupEstimateResultBO.getExternalUserCount());
            userGroupEstimateResponse.setMemberCount(userGroupEstimateResultBO.getMemberCount());
        }
        log.info("query user group estimate response. corpId={}, userGroupEstimateResponse={}", corpId,
                JsonUtils.toJSONString(userGroupEstimateResponse));
        return BaseResponse.success(userGroupEstimateResponse);
    }

    @Override
    public BaseResponse queryUserGroup(String projectId, String corpId, String taskType, String groupUuid) {

        WeComUserGroupAudienceEntity weComUserGroupAudienceEntity =
                weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByUuid(groupUuid);
        if (weComUserGroupAudienceEntity == null) {
            log.error("failed to query user group message is empty.");
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_QUERY_USER_GROUP_MESSAGE_IS_EMPTY);
        }
        UserGroupMessageResponse userGroupMessageResponse = new UserGroupMessageResponse();
        UserGroupAudienceRules audienceRules = new UserGroupAudienceRules();

        audienceRules.setUserGroupType(weComUserGroupAudienceEntity.getUserGroupType());
        if (weComUserGroupAudienceEntity.getUserGroupType().equals(UserGroupAudienceTypeEnum.WECOM_USER_GROUP.getValue())) {
            String conditions = weComUserGroupAudienceEntity.getWecomConditions();
            if (StringUtils.isNotBlank(conditions)) {
                WeComUserGroupRule weComUserGroupRule = JsonUtils.toObject(conditions, WeComUserGroupRule.class);
                audienceRules.setWeComUserGroupRule(weComUserGroupRule);
            }
        }
        userGroupMessageResponse.setUserGroup(audienceRules);
        userGroupMessageResponse.setUuid(groupUuid);
        String result = weComUserGroupAudienceEntity.getEstimateResult();
        if (StringUtils.isNotBlank(result)) {
            UserGroupEstimateResultBO userGroupEstimateResultBO = JsonUtils.toObject(result,
                    UserGroupEstimateResultBO.class);
            userGroupMessageResponse.setExternalUserCount(userGroupEstimateResultBO.getExternalUserCount());
            userGroupMessageResponse.setMemberCount(userGroupEstimateResultBO.getMemberCount());
        }
        log.info("query user group message response. groupUuid={}, audienceRules={}", groupUuid,
                JsonUtils.toJSONString(userGroupMessageResponse));
        return BaseResponse.success(userGroupMessageResponse);
    }

    @Override
    public BaseResponse offlineUserGroup(String projectId, String corpId, String groupUuid, String fileType,
                                         MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();
        log.error("upload csv file. fileName={}, fileSize={}, type={}", fileName, multipartFile.getSize(),
                multipartFile.getContentType());
        if (StringUtils.isBlank(fileName)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_UPLOAD_OFFLINE_USER_GROUP_FILE_NAME_EMPTY);
        }
        if (multipartFile.getSize() <= 0) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_UPLOAD_OFFLINE_USER_GROUP_FILE_SIZE_EMPTY);
        }

        try {
            EasyExcel.read(multipartFile.getInputStream(), OfflineUserGroupMessage.class,
                    new UploadOfflineDataListener(projectId, corpId, groupUuid)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BaseResponse.success();
    }

    @Override
    public BaseResponse getExcelTemplate(String projectId, String corpId, HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("application/csv;charset=gb18030");
        httpServletResponse.setCharacterEncoding("utf-8");
        // ??????URLEncoder.encode???????????????????????? ?????????easyexcel????????????
        String fileName = null;
        try {
            fileName = URLEncoder.encode("template", "UTF-8").replaceAll("\\+", "%20");

            httpServletResponse.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".csv");

            EasyExcel.write(httpServletResponse.getOutputStream(), OfflineUserGroupMessage.class).sheet("template").doWrite(templateData());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseResponse deleteOfflineUserGroup(String corpId, String groupUuid) {
        userGroupOfflineRepository.deleteByUuid(corpId, groupUuid);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse queryCrowdList(String projectId, String corpId) {
        List<CdpConfigEntity> entities = cdpConfigRepository.getCdpConfigByCorpId(projectId, corpId);
        if (CollectionUtils.isEmpty(entities)) {
            return BaseResponse.success();
        }
        CdpCrowdListResponse response = new CdpCrowdListResponse();
        CdpConfigEntity entity = entities.get(0);
        String cdpType = entity.getCdpType();
        if (StringUtils.isNotBlank(cdpType)) {
            CdpCrowdListMessage message = cdpManagerService.queryCrowdList(projectId, corpId);
            if (message != null && CollectionUtils.isNotEmpty(message.getCrowds())) {
                response.setCdpType(message.getCdpType());
                List<CdpCrowdListResponse.CrowdMessage> crowdMessageList = new ArrayList<>();
                for (CdpCrowdListMessage.CrowdMessage item : message.getCrowds()) {
                    CdpCrowdListResponse.CrowdMessage crowdMessage = new CdpCrowdListResponse.CrowdMessage();
                    BeanUtils.copyProperties(item, crowdMessage);
                    crowdMessageList.add(crowdMessage);
                }
                response.setCrowds(crowdMessageList);
            }
        } else {
            log.info("cdp type is empty. entity={}", entity);
        }
        return BaseResponse.success(response);
    }

    private List<OfflineUserGroupMessage> templateData() {
        List<OfflineUserGroupMessage> list = ListUtils.newArrayList();
        OfflineUserGroupMessage data = new OfflineUserGroupMessage();
        data.setExternalUserId("wmqPhANwAADkwwqT4B2as3tN4E6-6suA");
        data.setMemberId("WangWanZheng");
        list.add(data);
        return list;
    }

    private void saveOfflineUserGroup(List<UserGroupOfflineEntity> entities) {
        userGroupOfflineRepository.saveAll(entities);
    }

    public class UploadOfflineDataListener implements ReadListener<OfflineUserGroupMessage> {

        private static final int BATCH_COUNT = 100;
        private List<UserGroupOfflineEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

        private String projectId;
        private String corpId;
        private String groupUuid;

        public UploadOfflineDataListener(String projectId, String corpId, String groupUuid) {
            this.projectId = projectId;
            this.corpId = corpId;
            this.groupUuid = groupUuid;
        }

        @Override
        public void invoke(OfflineUserGroupMessage offlineUserGroupMessage, AnalysisContext analysisContext) {
            log.info("read csv data. offlineUserGroupRul={}", offlineUserGroupMessage);
            UserGroupOfflineEntity entity = new UserGroupOfflineEntity();
            entity.setCorpId(corpId);
            entity.setExternalUserId(offlineUserGroupMessage.getExternalUserId());
            entity.setMemberId(offlineUserGroupMessage.getMemberId());
            entity.setUuid(groupUuid);
            cachedDataList.add(entity);
            // ??????BATCH_COUNT????????????????????????????????????????????????????????????????????????????????????OOM
            if (cachedDataList.size() >= BATCH_COUNT) {
                saveOfflineUserGroup(cachedDataList);
                // ?????????????????? list
                cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            saveOfflineUserGroup(cachedDataList);
        }
    }

    public class WeComUserGroupEstimate implements Runnable {
        private WeComUserGroupRule rule;
        private String corpId;
        private String requestId;
        private String projectId;
        private String taskType;

        public WeComUserGroupEstimate(String projectId, String corpId, String taskType, String requestId,
                                      WeComUserGroupRule rule) {
            this.rule = rule;
            this.corpId = corpId;
            this.taskType = taskType;
            this.requestId = requestId;
            this.projectId = projectId;
        }

        @Override
        public void run() {
            Integer memberCount = 0;
            Integer externalUserCount = 0;
            try {
                List<Long> departments = new ArrayList<>();
                List<String> memberList = new ArrayList<>();
                if (!rule.getMembers().getIsAll()) {
                    //????????????????????????????????????
                    if (CollectionUtils.isNotEmpty(rule.getMembers().getDepartments())) {
                        rule.getMembers().getDepartments().forEach(department -> {
                            departments.add(department.getId());
                        });

                        List<WeComDepartmentEntity> departmentEntities =
                                weComDepartmentRepository.findByParentIdIn(departments);
                        while (CollectionUtils.isNotEmpty(departmentEntities)) {
                            List<Long> departmentList = new ArrayList<>();
                            departmentEntities.forEach(entity -> {
                                departmentList.add(entity.getDepartmentId());
                            });
                            log.info("user group find department list. departmentList={}", departmentList);
                            departments.addAll(departmentList);
                            departmentEntities =
                                    weComDepartmentRepository.findByParentIdIn(departmentList);
                        }

                        //?????????????????????
                        QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                                QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departments).build();
                        List<WeComMemberMessageEntity> entities =
                                weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
                        if (CollectionUtils.isNotEmpty(entities)) {
                            entities.forEach(entity -> {
                                memberList.add(entity.getMemberId());
                            });
                        }
                    }
                    //????????????????????????????????????
                    if (CollectionUtils.isNotEmpty(rule.getMembers().getUsers())) {
                        rule.getMembers().getUsers().forEach(user -> {
                            memberList.add(user.getMemberId());
                        });
                        memberCount = memberList.size();
                    }
                }

                List<String> distinctMemberList = memberList.stream().distinct().collect(Collectors.toList());
                memberCount = distinctMemberList.size();
                log.info("user group for member estimate result. memberCount={}, distinctMemberList={}", memberCount,
                        distinctMemberList);

                if (taskType.equals(WeComMassTaskTypeEnum.GROUP.name())) {
                    externalUserCount = weComGroupChatsRepository.countByOwner(corpId, distinctMemberList);
                    log.info("user group to query group chat count. groupChatCount={}", externalUserCount);
                } else {
                    QueryUserGroupBuildSqlParam paramBuilder =
                            QueryUserGroupBuildSqlParam.builder().corpId(corpId).memberIds(distinctMemberList).build();
                    paramBuilder.setRelation(rule.getExternalUsers().getRelation());
                    if (!rule.getExternalUsers().getIsAll()) {
                        //?????????????????????
                        if (rule.getExternalUsers().isAddTimeSwitch()) {
                            paramBuilder.setStartTime(rule.getExternalUsers().getStartTime());
                            paramBuilder.setEndTime(rule.getExternalUsers().getEndTime());
                        }

                        //????????????
                        if (rule.getExternalUsers().isCorpTagSwitch()
                                && rule.getExternalUsers().getCorpTags() != null && CollectionUtils.isNotEmpty(rule.getExternalUsers().getCorpTags().getTags())) {
                            paramBuilder.setTagRelation(rule.getExternalUsers().getCorpTags().getRelation());
                            List<String> tags = new ArrayList<>();
                            rule.getExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                                tags.add(weComCorpTag.getId());
                            });

                            paramBuilder.setTags(tags);
                        }

                        //????????????
                        if (rule.getExternalUsers().isGenderSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExternalUsers().getGenders())) {
                            paramBuilder.setGenders(rule.getExternalUsers().getGenders());
                        }

                        //???????????????
                        if (rule.getExternalUsers().isGroupChatsSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExternalUsers().getGroupChats())) {
                            List<String> groups = new ArrayList<>();
                            rule.getExternalUsers().getGroupChats().forEach(group -> {
                                groups.add(group.getChatId());
                            });

                            paramBuilder.setGroupChats(groups);
                        }
                    }
                    //??????????????????
                    if (rule.isExcludeSwitch()) {
                        //?????????????????????????????????
                        paramBuilder.setExcludeRelation(rule.getExcludeExternalUsers().getRelation());
                        if (rule.getExcludeExternalUsers().isAddTimeSwitch()) {
                            paramBuilder.setExcludeStartTime(rule.getExcludeExternalUsers().getStartTime());
                            paramBuilder.setExcludeEndTime(rule.getExcludeExternalUsers().getEndTime());
                        }

                        //?????????????????????????????????
                        if (rule.getExcludeExternalUsers().isCorpTagSwitch()
                                && rule.getExcludeExternalUsers().getCorpTags() != null
                                && CollectionUtils.isNotEmpty(rule.getExcludeExternalUsers().getCorpTags().getTags())) {
                            paramBuilder.setExcludeTagRelation(rule.getExcludeExternalUsers().getCorpTags().getRelation());
                            List<String> tags = new ArrayList<>();
                            rule.getExcludeExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                                tags.add(weComCorpTag.getId());
                            });
                            paramBuilder.setExcludeTags(tags);
                        }

                        //?????????????????????????????????
                        if (rule.getExcludeExternalUsers().isGenderSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExcludeExternalUsers().getGenders())) {
                            paramBuilder.setExcludeGenders(rule.getExcludeExternalUsers().getGenders());
                        }

                        //????????????????????????????????????
                        if (rule.getExcludeExternalUsers().isGroupChatsSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExcludeExternalUsers().getGroupChats())) {
                            List<String> groups = new ArrayList<>();
                            rule.getExcludeExternalUsers().getGroupChats().forEach(group -> {
                                groups.add(group.getChatId());
                            });

                            paramBuilder.setExcludeGroupChats(groups);
                        }
                    }
                    externalUserCount = weComRelationMemberExternalUserRepository.countByUserGroupCnd(paramBuilder);
                    log.info("user group for external user estimate result. externalUserCount={}", externalUserCount);

                }
            } catch (Exception e) {
                log.error("failed to user group for external user estimate result. requestId={}", requestId, e);
            }
            UserGroupEstimateResultBO userGroupEstimateResultBO = new UserGroupEstimateResultBO();
            userGroupEstimateResultBO.setExternalUserCount(externalUserCount);
            userGroupEstimateResultBO.setMemberCount(memberCount);

            weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                    JsonUtils.toJSONString(userGroupEstimateResultBO),
                    UserGroupAudienceStatusEnum.SUCCEED.getValue());
        }
    }

    public class OfflineUserGroupEstimate implements Runnable {
        private OfflineUserGroupRule rule;
        private String corpId;
        private String requestId;
        private String projectId;
        private String taskType;

        public OfflineUserGroupEstimate(String projectId, String corpId, String taskType, String requestId,
                                        OfflineUserGroupRule rule) {
            this.rule = rule;
            this.corpId = corpId;
            this.taskType = taskType;
            this.requestId = requestId;
            this.projectId = projectId;
        }

        @Override
        public void run() {
            Integer memberCount = 0;
            Integer externalUserCount = 0;
            try {
                externalUserCount = userGroupOfflineRepository.queryExternalUserCountByUuid(corpId,
                        rule.getUserGroupUuid());
                memberCount = userGroupOfflineRepository.queryMemberCountByUuid(corpId,
                        rule.getUserGroupUuid());
            } catch (Exception e) {
                log.error("failed to user group for external user estimate result. requestId={}", requestId, e);
            }
            UserGroupEstimateResultBO userGroupEstimateResultBO = new UserGroupEstimateResultBO();
            userGroupEstimateResultBO.setExternalUserCount(externalUserCount);
            userGroupEstimateResultBO.setMemberCount(memberCount);

            weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                    JsonUtils.toJSONString(userGroupEstimateResultBO),
                    UserGroupAudienceStatusEnum.SUCCEED.getValue());
        }
    }

    public class CdpUserGroupEstimate implements Runnable {
        private CdpUserGroupRule rule;
        private String corpId;
        private String requestId;
        private String projectId;
        private String taskType;

        public CdpUserGroupEstimate(String projectId, String corpId, String taskType, String requestId,
                                    CdpUserGroupRule rule) {
            this.rule = rule;
            this.corpId = corpId;
            this.taskType = taskType;
            this.requestId = requestId;
            this.projectId = projectId;
        }

        @Override
        public void run() {
            Integer memberCount = 0;
            Integer externalUserCount = 0;
            try {
                if (CollectionUtils.isNotEmpty(rule.getCrowds())) {
                    for (CdpUserGroupRule.CrowdMessage message : rule.getCrowds()) {
                        memberCount += message.getUserCount();
                        externalUserCount += message.getUserCount();
                    }
                }
            } catch (Exception e) {
                log.error("failed to user group for external user estimate result. requestId={}", requestId, e);
            }
            UserGroupEstimateResultBO userGroupEstimateResultBO = new UserGroupEstimateResultBO();
            userGroupEstimateResultBO.setExternalUserCount(externalUserCount);
            userGroupEstimateResultBO.setMemberCount(memberCount);

            weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                    JsonUtils.toJSONString(userGroupEstimateResultBO),
                    UserGroupAudienceStatusEnum.SUCCEED.getValue());
        }
    }
}
