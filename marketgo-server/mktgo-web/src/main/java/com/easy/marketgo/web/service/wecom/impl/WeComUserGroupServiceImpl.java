package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.text.csv.CsvReader;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.bo.UserGroupEstimateResultBO;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.service.WeComUserGroupFactoryService;
import com.easy.marketgo.web.model.bo.OfflineUserGroupRule;
import com.easy.marketgo.web.model.bo.WeComUserGroupRule;
import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.web.model.response.BaseResponse;
import com.easy.marketgo.web.model.response.UserGroupEstimateResponse;
import com.easy.marketgo.web.model.response.UserGroupMessageResponse;
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
    private WeComUserGroupFactoryService weComUserGroupFactoryService;

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
            }
            weComUserGroupAudienceEntity.setConditionsRelation(audienceRules.getRelation());
            weComUserGroupAudienceEntity.setStatus(UserGroupAudienceStatusEnum.COMPUTING.getValue());
            weComUserGroupAudienceRepository.save(weComUserGroupAudienceEntity);

            Runnable task =
                    new WeComUserGroupEstimate(projectId, corpId, taskType, requestId,
                            audienceRules.getWeComUserGroupRule());
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
        CsvReader r = null;
        return null;
    }

    @Override
    public BaseResponse getExcelTemplate(String projectId, String corpId, HttpServletResponse httpServletResponse) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpServletResponse.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = null;
        try {
            fileName = URLEncoder.encode("template", "UTF-8").replaceAll("\\+", "%20");

            httpServletResponse.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".csv");

            EasyExcel.write(httpServletResponse.getOutputStream(), OfflineUserGroupRule.class).sheet("template").doWrite(templeteData());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<OfflineUserGroupRule> templeteData() {
        List<OfflineUserGroupRule> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            OfflineUserGroupRule data = new OfflineUserGroupRule();
            data.setExternalUserId("wmqPhANwAADkwwqT4B2as3tN4E6-6suA");
            data.setMemberId("WangWanZheng");
            list.add(data);
        }
        return list;
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
                    //获取人群条件中的部门列表
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

                        //计算员工的数量
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
                    //获取人群条件中的员工列表
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
                        //添加时间的条件
                        if (rule.getExternalUsers().isAddTimeSwitch()) {
                            paramBuilder.setStartTime(rule.getExternalUsers().getStartTime());
                            paramBuilder.setEndTime(rule.getExternalUsers().getEndTime());
                        }

                        //标签条件
                        if (rule.getExternalUsers().isCorpTagSwitch()
                                && rule.getExternalUsers().getCorpTags() != null && CollectionUtils.isNotEmpty(rule.getExternalUsers().getCorpTags().getTags())) {
                            paramBuilder.setTagRelation(rule.getExternalUsers().getCorpTags().getRelation());
                            List<String> tags = new ArrayList<>();
                            rule.getExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                                tags.add(weComCorpTag.getId());
                            });

                            paramBuilder.setTags(tags);
                        }

                        //性别条件
                        if (rule.getExternalUsers().isGenderSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExternalUsers().getGenders())) {
                            paramBuilder.setGenders(rule.getExternalUsers().getGenders());
                        }

                        //客户群条件
                        if (rule.getExternalUsers().isGroupChatsSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExternalUsers().getGroupChats())) {
                            List<String> groups = new ArrayList<>();
                            rule.getExternalUsers().getGroupChats().forEach(group -> {
                                groups.add(group.getChatId());
                            });

                            paramBuilder.setGroupChats(groups);
                        }
                    }
                    //排除客户条件
                    if (rule.isExcludeSwitch()) {
                        //添加排除客户的时间条件
                        paramBuilder.setExcludeRelation(rule.getExcludeExternalUsers().getRelation());
                        if (rule.getExcludeExternalUsers().isAddTimeSwitch()) {
                            paramBuilder.setExcludeStartTime(rule.getExcludeExternalUsers().getStartTime());
                            paramBuilder.setExcludeEndTime(rule.getExcludeExternalUsers().getEndTime());
                        }

                        //添加排除客户的标签条件
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

                        //添加排除客户的性别条件
                        if (rule.getExcludeExternalUsers().isGenderSwitch() &&
                                CollectionUtils.isNotEmpty(rule.getExcludeExternalUsers().getGenders())) {
                            paramBuilder.setExcludeGenders(rule.getExcludeExternalUsers().getGenders());
                        }

                        //添加排除客户的客户群条件
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
}
