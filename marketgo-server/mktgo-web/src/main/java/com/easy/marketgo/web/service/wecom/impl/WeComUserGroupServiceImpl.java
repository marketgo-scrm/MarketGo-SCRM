package com.easy.marketgo.web.service.wecom.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.easy.marketgo.core.model.cdp.CdpCrowdListMessage;
import com.easy.marketgo.core.service.cdp.CdpManagerService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.cdp.CdpConfigEntity;
import com.easy.marketgo.core.entity.masstask.WeComUserGroupAudienceEntity;
import com.easy.marketgo.core.entity.usergroup.UserGroupOfflineEntity;
import com.easy.marketgo.core.model.usergroup.UserGroupEstimateResult;
import com.easy.marketgo.core.repository.cdp.CdpConfigRepository;
import com.easy.marketgo.core.repository.usergroup.UserGroupOfflineRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.biz.service.wecom.usergroup.UserGroupMangerService;
import com.easy.marketgo.web.model.bo.OfflineUserGroupMessage;
import com.easy.marketgo.web.model.bo.WeComUserGroupRule;
import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.UserGroupEstimateResponse;
import com.easy.marketgo.web.model.response.UserGroupMessageResponse;
import com.easy.marketgo.web.model.response.cdp.CdpCrowdListResponse;
import com.easy.marketgo.web.service.wecom.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;


    @Autowired
    private UserGroupOfflineRepository userGroupOfflineRepository;

    @Autowired
    private CdpManagerService cdpManagerService;

    @Autowired
    private UserGroupMangerService userGroupMangerService;

    @Override
    public BaseResponse estimate(String projectId, UserGroupAudienceRules audienceRules, String requestId,
                                 String corpId, String taskType) {

        if (audienceRules.getUserGroupType().equals(UserGroupAudienceTypeEnum.WECOM_USER_GROUP.getValue()) &&
                audienceRules.getWeComUserGroupRule().getMembers() == null) {
            log.error("member message is empty for user group estimate.");
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
        }

        if (audienceRules.getUserGroupType().equals(UserGroupAudienceTypeEnum.OFFLINE_USER_GROUP.getValue()) &&
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
            } else if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.OFFLINE_USER_GROUP.getValue())) {
                weComUserGroupAudienceEntity.setOfflineConditions(JsonUtils.toJSONString(audienceRules.getOfflineUserGroupRule()));
            } else if (audienceRules.getUserGroupType().equalsIgnoreCase(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue())) {
                weComUserGroupAudienceEntity.setCdpConditions(JsonUtils.toJSONString(audienceRules.getCdpUserGroupRule()));
            }
            weComUserGroupAudienceEntity.setConditionsRelation(audienceRules.getRelation());
            weComUserGroupAudienceEntity.setStatus(UserGroupAudienceStatusEnum.COMPUTING.getValue());
            weComUserGroupAudienceRepository.save(weComUserGroupAudienceEntity);

            userGroupMangerService.userGroupEstimate(projectId, requestId, corpId, taskType,
                    JsonUtils.toJSONString(audienceRules));
            entity = weComUserGroupAudienceRepository.queryWeComUserGroupAudienceEntityByRequestId(projectId,
                    requestId);
        }

        BeanUtils.copyProperties(entity, userGroupEstimateResponse);
        String result = entity.getEstimateResult();
        if (StringUtils.isNotBlank(result)) {
            UserGroupEstimateResult userGroupEstimateResult = JsonUtils.toObject(result,
                    UserGroupEstimateResult.class);
            userGroupEstimateResponse.setExternalUserCount(userGroupEstimateResult.getExternalUserCount());
            userGroupEstimateResponse.setMemberCount(userGroupEstimateResult.getMemberCount());
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
            UserGroupEstimateResult userGroupEstimateResult = JsonUtils.toObject(result,
                    UserGroupEstimateResult.class);
            userGroupMessageResponse.setExternalUserCount(userGroupEstimateResult.getExternalUserCount());
            userGroupMessageResponse.setMemberCount(userGroupEstimateResult.getMemberCount());
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
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
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
            // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
            if (cachedDataList.size() >= BATCH_COUNT) {
                saveOfflineUserGroup(cachedDataList);
                // 存储完成清理 list
                cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            saveOfflineUserGroup(cachedDataList);
        }
    }
}
