package com.easy.marketgo.web.service.wecom.impl;

import com.easy.marketgo.api.model.request.tag.WeComAddCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComDeleteCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComEditCorpTagsClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.tag.WeComQueryCorpTagsClientResponse;
import com.easy.marketgo.api.service.WeComTagsRpcService;
import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.biz.service.wecom.tag.MarkCorpTagsProducer;
import com.easy.marketgo.biz.service.wecom.tag.SyncCorpTagService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.tag.WeComCorpTagEntity;
import com.easy.marketgo.core.model.bo.WeComMarkCorpTags;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComCorpTagRepository;
import com.easy.marketgo.web.model.request.tags.WeComAddCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComDeleteCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComEditCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComMarkCorpTagsRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.WeComCoreTagsResponse;
import com.easy.marketgo.web.service.wecom.CorpTagsManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.easy.marketgo.common.constants.Constants.AGENT_KEY_FOR_EXTERNALUSER;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/19/22 4:49 PM
 * Describe:
 */

@Slf4j
@Service
public class CorpTagsManagerServiceImpl implements CorpTagsManagerService {

    @Autowired
    private WeComCorpTagRepository weComCorpTagRepository;

    @Resource
    private WeComTagsRpcService weComTagsRpcService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;
    @Autowired
    private SyncCorpTagService syncCorpTagService;

    @Autowired
    private MarkCorpTagsProducer markCorpTagsProducer;

    @Override
    public BaseResponse markCorpTags(String projectId, String corpId, WeComMarkCorpTagsRequest request) {
        log.info("start to mark corp tags. projectId={}, corpId={},  request={}", projectId, corpId, request);
        WeComMarkCorpTags tags = new WeComMarkCorpTags();
        tags.setCorpId(corpId);
        tags.setAgentId(AGENT_KEY_FOR_EXTERNALUSER);
        tags.setTagIds(request.getMarkCorpTags());
        if (CollectionUtils.isNotEmpty(request.getExternalUsers())) {
            List<WeComMarkCorpTags.ExternalUserAndMember> list = new ArrayList<>();
            request.getExternalUsers().forEach(item -> {
                WeComMarkCorpTags.ExternalUserAndMember message = new WeComMarkCorpTags.ExternalUserAndMember();
                message.setExternalUserId(item.getExternalUserId());
                message.setMemberId(item.getMemberId());
                list.add(message);
            });
            tags.setExternalUsers(list);
        }

        markCorpTagsProducer.markCorpTagForExternalUser(tags);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse addCorpTags(String projectId, String corpId, WeComAddCorpTagRequest request) {

        if (CollectionUtils.isEmpty(request.getTagNameList())) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_MASS_TASK_IS_EMPTY);
        }
        BaseResponse baseResponse = BaseResponse.success();
        WeComAgentMessageEntity agentMessageEntity = weComAgentMessageRepository.getWeComAgentByCorp(projectId, corpId);
        log.info("query agent message. agentMessageEntity={}", agentMessageEntity);
        String agentId = (agentMessageEntity != null) ? agentMessageEntity.getAgentId() : "";

        WeComAddCorpTagsClientRequest weComAddCorpTagsClientRequest = new WeComAddCorpTagsClientRequest();
        weComAddCorpTagsClientRequest.setCorpId(corpId);
        weComAddCorpTagsClientRequest.setAgentId(agentId);

        if (StringUtils.isNotEmpty(request.getGroupName())) {
            weComAddCorpTagsClientRequest.setGroupName(request.getGroupName());
        } else if (StringUtils.isNotEmpty(request.getGroupId())) {
            weComAddCorpTagsClientRequest.setGroupId(request.getGroupId());
        }
        List<WeComAddCorpTagsClientRequest.TagMessage> tags = new ArrayList<>();
        request.getTagNameList().forEach(item -> {
            WeComAddCorpTagsClientRequest.TagMessage message = new WeComAddCorpTagsClientRequest.TagMessage();
            message.setName(item);
            tags.add(message);
        });
        weComAddCorpTagsClientRequest.setTag(tags);
        RpcResponse response = weComTagsRpcService.addCorpTags(weComAddCorpTagsClientRequest);
        log.info("add corp tags response. RpcResponse={}", response);
        if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
            xxlJobManualTriggerService.manualTriggerHandler("syncCorpTags");
        }
        baseResponse.setCode(response.getCode());
        baseResponse.setMessage(response.getMessage());
        return baseResponse;
    }

    @Override
    public BaseResponse deleteCorpTags(String projectId, String corpId, WeComDeleteCorpTagRequest request) {
        log.info("delete corp tags. corpId={}, request={}", corpId, request);
        if (CollectionUtils.isEmpty(request.getGroupId()) && CollectionUtils.isEmpty(request.getTagId())) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_DELETE_TAG_IS_EMPTY);
        }
        BaseResponse baseResponse = BaseResponse.success();

        WeComDeleteCorpTagsClientRequest weComDeleteCorpTagsClientRequest = new WeComDeleteCorpTagsClientRequest();
        weComDeleteCorpTagsClientRequest.setAgentId(AGENT_KEY_FOR_EXTERNALUSER);
        weComDeleteCorpTagsClientRequest.setCorpId(corpId);
        if (CollectionUtils.isNotEmpty(request.getGroupId())) {
            weComDeleteCorpTagsClientRequest.setGroupId(request.getGroupId());
        }
        if (CollectionUtils.isNotEmpty(request.getTagId())) {
            weComDeleteCorpTagsClientRequest.setTagId(request.getTagId());
        }
        RpcResponse response = weComTagsRpcService.deleteCorpTags(weComDeleteCorpTagsClientRequest);
        log.info("delete corp tags response. RpcResponse={}", response);
        if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
            xxlJobManualTriggerService.manualTriggerHandler("syncCorpTags");
        }
        baseResponse.setCode(response.getCode());
        baseResponse.setMessage(response.getMessage());
        return baseResponse;
    }

    @Override
    public BaseResponse editCorpTags(String projectId, String corpId, WeComEditCorpTagRequest request) {
        log.info("edit corp tags. corpId={}, request={}", corpId, request);
        if (CollectionUtils.isEmpty(request.getGroups()) && CollectionUtils.isEmpty(request.getTags())) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_EDIT_TAG_IS_EMPTY);
        }
        BaseResponse baseResponse = BaseResponse.success();

        WeComEditCorpTagsClientRequest weComEditCorpTagsClientRequest = new WeComEditCorpTagsClientRequest();
        weComEditCorpTagsClientRequest.setAgentId(AGENT_KEY_FOR_EXTERNALUSER);
        weComEditCorpTagsClientRequest.setCorpId(corpId);
        if (CollectionUtils.isNotEmpty(request.getTags())) {
            for (WeComEditCorpTagRequest.TagMessage group : request.getGroups()) {
                Integer count = weComCorpTagRepository.countByCorpIdAndTagId(corpId, group.getId(), group.getName());
                if (count == null || count == 0) {
                    weComEditCorpTagsClientRequest.setId(group.getId());
                    weComEditCorpTagsClientRequest.setName(group.getName());
                    RpcResponse response = weComTagsRpcService.editCorpTags(weComEditCorpTagsClientRequest);
                    log.info("edit corp group tags response. RpcResponse={}", response);
                    if (response.getCode().equals(ErrorCodeEnum.ERROR_WECOM_EDIT_TAG_IS_DENIED.getCode())) {
                        return BaseResponse.failure(ErrorCodeEnum.ERROR_WECOM_EDIT_TAG_IS_DENIED);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(request.getTags())) {
            for (WeComEditCorpTagRequest.TagMessage tag : request.getTags()) {
                Integer count = weComCorpTagRepository.countByCorpIdAndTagId(corpId, tag.getId(), tag.getName());
                if (count == null || count == 0) {
                    weComEditCorpTagsClientRequest.setId(tag.getId());
                    weComEditCorpTagsClientRequest.setName(tag.getName());
                    RpcResponse response = weComTagsRpcService.editCorpTags(weComEditCorpTagsClientRequest);
                    log.info("edit corp tags response. RpcResponse={}", response);
                    if (response.getCode().equals(ErrorCodeEnum.ERROR_WECOM_EDIT_TAG_IS_DENIED.getCode())) {
                        return BaseResponse.failure(ErrorCodeEnum.ERROR_WECOM_EDIT_TAG_IS_DENIED);
                    }
                }
            }
        }
        xxlJobManualTriggerService.manualTriggerHandler("syncCorpTags");
        baseResponse.setCode(ErrorCodeEnum.OK.getCode());
        baseResponse.setMessage(ErrorCodeEnum.OK.getMessage());
        return baseResponse;
    }

    @Override
    public BaseResponse listCorpTags(String projectId, String corpId, String keyword, String syncStatus) {
        log.info("query corp tags. corpId={}, keyword={}, syncStatus={}", corpId, keyword, syncStatus);

        if (syncStatus.equals("sync")) {
            return listCorpTagsSync(corpId);
        }
        if (StringUtils.isNotEmpty(keyword)) {
            return listCorpTagsForKeyword(corpId, keyword);
        }
        List<WeComCorpTagEntity> entities = weComCorpTagRepository.queryByCorpIdAndType(corpId, Boolean.FALSE);
        WeComCoreTagsResponse response = new WeComCoreTagsResponse();
        List<WeComCoreTagsResponse.WeComCorpTagGroup> tagGroups = new ArrayList<>();
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query corp tags is empty. corpId={}", corpId);
            response.setCorpTagGroups(tagGroups);
            return BaseResponse.success(response);
        }

        entities.forEach(entity -> {
            WeComCoreTagsResponse.WeComCorpTagGroup tagGroup = new WeComCoreTagsResponse.WeComCorpTagGroup();
            tagGroup.setGroupId(entity.getTagId());
            tagGroup.setGroupName(entity.getName());
            tagGroup.setOrder(entity.getOrder());
            List<WeComCorpTagEntity> tagEntities = weComCorpTagRepository.queryTagByCorpIdAndType(corpId, Boolean.TRUE,
                    entity.getTagId());
            log.info("query corp tags from db. corpId={}, tagEntities={}", corpId, tagEntities);
            List<WeComCoreTagsResponse.WeComCorpTag> tags = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tagEntities)) {
                tagEntities.forEach(tagEntity -> {
                    WeComCoreTagsResponse.WeComCorpTag corpTag = new WeComCoreTagsResponse.WeComCorpTag();
                    corpTag.setId(tagEntity.getTagId());
                    corpTag.setName(tagEntity.getName());
                    corpTag.setOrder(tagEntity.getOrder());
                    tags.add(corpTag);
                });
            }
            tagGroup.setTags(tags);
            tagGroups.add(tagGroup);
        });
        response.setCorpTagGroups(tagGroups);
        log.info("query corp tags response. corpId={}, response={}", corpId, response);
        return BaseResponse.success(response);
    }

    private BaseResponse listCorpTagsSync(String corpId) {
        WeComCoreTagsResponse response = new WeComCoreTagsResponse();
        List<WeComCoreTagsResponse.WeComCorpTagGroup> tagGroups = new ArrayList<>();


        WeComQueryCorpTagsClientResponse data = syncCorpTagService.queryCorpTagsForCorpId(corpId);
        if (data == null) {
            log.info("response WeComQueryCorpTagsClientResponse is empty.");
            response.setCorpTagGroups(tagGroups);
            return BaseResponse.success(response);
        }

        data.getTagGroup().forEach(item -> {
            WeComCoreTagsResponse.WeComCorpTagGroup tagGroup = new WeComCoreTagsResponse.WeComCorpTagGroup();
            if (item.getDeleted() == null) {
                tagGroup.setGroupId(item.getGroupId());
                tagGroup.setGroupName(item.getGroupName());
                tagGroup.setOrder(item.getOrder());
            }
            List<WeComCoreTagsResponse.WeComCorpTag> tags = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(item.getTag())) {
                item.getTag().forEach(tagItem -> {
                    WeComCoreTagsResponse.WeComCorpTag tag = new WeComCoreTagsResponse.WeComCorpTag();
                    tag.setId(tagItem.getId());
                    tag.setName(tagItem.getName());
                    tag.setOrder(tagItem.getOrder());
                    tags.add(tag);
                });
            }
            tagGroup.setTags(tags);
            tagGroups.add(tagGroup);
        });
        response.setCorpTagGroups(tagGroups);
        log.info("query corp tags from weCom response. corpId={}, response={}", corpId, response);
        return BaseResponse.success(response);
    }

    private BaseResponse listCorpTagsForKeyword(String corpId, String keyword) {
        WeComCoreTagsResponse response = new WeComCoreTagsResponse();
        List<WeComCorpTagEntity> entities = weComCorpTagRepository.queryByCorpIdAndKeyword(corpId, keyword);
        log.info("query corp tag list. corpId={}, keyword={}, entities={}", corpId, keyword, entities);
        List<WeComCoreTagsResponse.WeComCorpTagGroup> tagGroups = new ArrayList<>();
        if (CollectionUtils.isEmpty(entities)) {
            response.setCorpTagGroups(tagGroups);
            return BaseResponse.success(response);
        }
        Map<String, List<WeComCorpTagEntity>> mapList =
                entities.stream().collect(Collectors.groupingBy(WeComCorpTagEntity::getGroupId));
        Iterator<Map.Entry<String, List<WeComCorpTagEntity>>> iterator =
                mapList.entrySet().iterator();
        while (iterator.hasNext()) {
            WeComCoreTagsResponse.WeComCorpTagGroup tagGroup =
                    new WeComCoreTagsResponse.WeComCorpTagGroup();

            List<WeComCoreTagsResponse.WeComCorpTag> tags = new ArrayList<>();
            Map.Entry<String, List<WeComCorpTagEntity>> item = iterator.next();
            String key = item.getKey();
            List<WeComCorpTagEntity> tagList = item.getValue();
            tagGroup.setGroupId(key);
            tagList.forEach(weComCorpTagEntity -> {
                if (weComCorpTagEntity.getType().equals(Boolean.TRUE)) {
                    WeComCoreTagsResponse.WeComCorpTag tag = new WeComCoreTagsResponse.WeComCorpTag();
                    tag.setId(weComCorpTagEntity.getTagId());
                    tag.setName(weComCorpTagEntity.getName());
                    tag.setOrder(weComCorpTagEntity.getOrder());
                    tags.add(tag);
                } else {
                    tagGroup.setGroupId(weComCorpTagEntity.getTagId());
                    tagGroup.setGroupName(weComCorpTagEntity.getName());
                    tagGroup.setOrder(weComCorpTagEntity.getOrder());
                }
            });

            if (CollectionUtils.isEmpty(tags)) {
                List<WeComCorpTagEntity> tagEntities = weComCorpTagRepository.queryTagByCorpIdAndType(corpId,
                        Boolean.TRUE,
                        key);
                log.info("query corp tags from db. corpId={}, tagEntities={}", corpId, tagEntities);
                if (CollectionUtils.isNotEmpty(tagEntities)) {
                    tagEntities.forEach(tagEntity -> {
                        WeComCoreTagsResponse.WeComCorpTag corpTag = new WeComCoreTagsResponse.WeComCorpTag();
                        corpTag.setId(tagEntity.getTagId());
                        corpTag.setName(tagEntity.getName());
                        corpTag.setOrder(tagEntity.getOrder());
                        tags.add(corpTag);
                    });
                }
            }
            if (StringUtils.isEmpty(tagGroup.getGroupName())) {
                WeComCorpTagEntity groupEntity = weComCorpTagRepository.queryByTagId(corpId, key);
                if (groupEntity != null) {
                    tagGroup.setGroupId(groupEntity.getTagId());
                    tagGroup.setGroupName(groupEntity.getName());
                    tagGroup.setOrder(groupEntity.getOrder());
                }
            }
            tagGroup.setTags(tags);
            tagGroups.add(tagGroup);
        }
        response.setCorpTagGroups(tagGroups);
        log.info("query corp tags response. corpId={}, response={}", corpId, response);
        return BaseResponse.success(response);
    }
}
