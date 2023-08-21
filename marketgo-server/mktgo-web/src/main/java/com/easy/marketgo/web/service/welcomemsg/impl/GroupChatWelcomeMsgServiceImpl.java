package com.easy.marketgo.web.service.welcomemsg.impl;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.WeComDeleteGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComGroupChatWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComGroupChatWelcomeMsgClientResponse;
import com.easy.marketgo.api.service.WeComWelcomeMsgRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.entity.welcomemsg.WeComWelcomeMsgGroupChatEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.model.bo.QueryChannelLiveCodeBuildSqlParam;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.welcomemsg.WeComWelcomeMsgGroupChatRepository;
import com.easy.marketgo.core.repository.welcomemsg.WeComWelcomeMsgGroupChatStatisticRepository;
import com.easy.marketgo.core.service.WeComAgentMessageService;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import com.easy.marketgo.web.model.request.welcomemsg.CustomerWelcomeMsgSaveRequest;
import com.easy.marketgo.web.model.request.welcomemsg.WelcomeMsgGroupChatSaveRequest;
import com.easy.marketgo.web.model.response.WeComCreateTaskBaseResponse;
import com.easy.marketgo.web.model.response.welcomemsg.WelcomeMsgGroupChatDetailResponse;
import com.easy.marketgo.web.model.response.welcomemsg.WelcomeMsgGroupChatListResponse;
import com.easy.marketgo.web.service.welcomemsg.GroupChatWelcomeMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/17/23 11:43 AM
 * Describe:
 */

@Component
@Slf4j
public class GroupChatWelcomeMsgServiceImpl implements GroupChatWelcomeMsgService {

    @Autowired
    private WeComWelcomeMsgGroupChatRepository weComWelcomeMsgGroupChatRepository;

    @Autowired
    private WeComWelcomeMsgGroupChatStatisticRepository weComWelcomeMsgGroupChatStatisticRepository;

    @Autowired
    private WeComAgentMessageRepository agentMessageRepository;

    @Resource
    private WeComWelcomeMsgRpcService weComWelcomeMsgRpcService;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Autowired
    private WeComAgentMessageService weComAgentMessageService;

    @Autowired
    private WeComMemberService weComMemberService;

    @Override
    public BaseResponse queryWelcomeMsgList(String projectUuid, String corpId, Integer pageNum, Integer pageSize,
                                            String keyword) {
        WelcomeMsgGroupChatListResponse response = new WelcomeMsgGroupChatListResponse();
        QueryChannelLiveCodeBuildSqlParam param = new QueryChannelLiveCodeBuildSqlParam();
        param.setProjectUuid(projectUuid);
        param.setCorpId(corpId);
        param.setKeyword(keyword);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        param.setSortOrderKey("DESC");
        Integer count = weComWelcomeMsgGroupChatRepository.countByBuildSqlParam(param);
        log.info("query welcome msg group chat count. count={}, param={}", count, param);

        List<WeComWelcomeMsgGroupChatEntity> entities = weComWelcomeMsgGroupChatRepository.listByBuildSqlParam(param);

        response.setTotalCount(count == null ? 0 : count);

        if (CollectionUtils.isNotEmpty(entities)) {
            List<WelcomeMsgGroupChatListResponse.WelcomeMsgDetail> infoList = entities.stream().map(entity -> {

                WelcomeMsgGroupChatListResponse.WelcomeMsgDetail welcomeMsg =
                        new WelcomeMsgGroupChatListResponse.WelcomeMsgDetail();
                BeanUtils.copyProperties(entity, welcomeMsg);
                welcomeMsg.setWelcomeContent(StringUtils.isBlank(entity.getWelcomeContent()) ? null :
                        JsonUtils.toArray(entity.getWelcomeContent(),
                                WeComMassTaskSendMsg.class));
                welcomeMsg.setCreateTime(DateUtil.formatDateTime(entity.getCreateTime()));
                return welcomeMsg;
            }).collect(toList());
            response.setList(infoList);
        }
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse createOrUpdate(String projectUuid, String corpId, WelcomeMsgGroupChatSaveRequest request) {
        WeComCreateTaskBaseResponse response = new WeComCreateTaskBaseResponse();
        WeComWelcomeMsgGroupChatEntity welcomeMsgGroupChatEntity = new WeComWelcomeMsgGroupChatEntity();
        log.info("start to save welcome msg group chat. projectUuid={}, corpId={} request={}, name={}",
                projectUuid, corpId, request);
        WeComGroupChatWelcomeMsgClientRequest clientRequest = new WeComGroupChatWelcomeMsgClientRequest();
        WeComAgentMessageEntity agent = agentMessageRepository.getWeComAgentByCorp(projectUuid, corpId);
        clientRequest.setAgentId(agent.getAgentId());
        clientRequest.setCorpId(corpId);
        if (StringUtils.isNotBlank(request.getUuid())) {
            welcomeMsgGroupChatEntity = weComWelcomeMsgGroupChatRepository.queryByCorpAndUuid(corpId,
                    request.getUuid());
            clientRequest.setTemplateId(welcomeMsgGroupChatEntity.getTemplateId());
        } else {
            welcomeMsgGroupChatEntity.setUuid(UuidUtils.generateUuid());
            welcomeMsgGroupChatEntity.setProjectUuid(projectUuid);
            welcomeMsgGroupChatEntity.setCorpId(corpId);

            clientRequest.setTemplateId("");
        }
        welcomeMsgGroupChatEntity.setWelcomeContent(JsonUtils.toJSONString(request.getWelcomeContent()));
        welcomeMsgGroupChatEntity.setCreatorId(request.getCreatorId());
        welcomeMsgGroupChatEntity.setCreatorName(request.getCreatorName());
        welcomeMsgGroupChatEntity.setNotifyType(request.getNotifyType());
        welcomeMsgGroupChatEntity.setName(request.getName());
        if (request.getNotifyType() == 2 && request.getMembers() != null) {
            welcomeMsgGroupChatEntity.setMembers(JsonUtils.toJSONString(request.getMembers()));

            List<String> members = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(request.getMembers().getDepartments())) {
                List<Long> departs =
                        request.getMembers().getDepartments().stream().map(CustomerWelcomeMsgSaveRequest.DepartmentMessage::getId).collect(toList());
                members.addAll(weComMemberService.getMembersForDepartment(corpId, departs));
            }
            if (CollectionUtils.isNotEmpty(request.getMembers().getUsers())) {
                members.addAll(request.getMembers().getUsers().stream().map(CustomerWelcomeMsgSaveRequest.UserMessage::getMemberId).collect(toList()));
            }
            weComAgentMessageService.sendWelcomeMsgRemindMessage(corpId, agent.getAgentId(),
                    welcomeMsgGroupChatEntity.getUuid(), members);

        } else {
            welcomeMsgGroupChatEntity.setMembers("");
        }
        clientRequest.setNotify(request.getNotifyType() == 1 ? 1 : 0);
        request.getWelcomeContent().forEach(weComMassTaskSendMsg -> {
            if (weComMassTaskSendMsg.getType() == WeComMassTaskSendMsg.TypeEnum.TEXT) {
                WeComSendWelcomeMsgClientRequest.TextMessage textMessage =
                        new WeComSendWelcomeMsgClientRequest.TextMessage();
                if (weComMassTaskSendMsg.getText() != null && StringUtils.isNotBlank(weComMassTaskSendMsg.getText().getContent())) {
                    textMessage.setContent(weComMassTaskSendMsg.getText().getContent());
                    clientRequest.setText(textMessage);
                }
            } else if (weComMassTaskSendMsg.getType() == WeComMassTaskSendMsg.TypeEnum.IMAGE) {
                WeComSendWelcomeMsgClientRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                        new WeComSendWelcomeMsgClientRequest.ImageAttachmentsMessage();
                if (StringUtils.isNotBlank(weComMassTaskSendMsg.getImage().getMediaUuid())) {
                    imageAttachmentsMessage.setMediaId(queryMediaId(weComMassTaskSendMsg.getImage().getMediaUuid()));
                    clientRequest.setImage(imageAttachmentsMessage);
                }
            } else if (weComMassTaskSendMsg.getType() == WeComMassTaskSendMsg.TypeEnum.LINK) {
                WeComSendWelcomeMsgClientRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                        new WeComSendWelcomeMsgClientRequest.LinkAttachmentsMessage();
                if (weComMassTaskSendMsg.getLink() != null &&
                        StringUtils.isNotBlank(weComMassTaskSendMsg.getLink().getTitle()) &&
                        StringUtils.isNotBlank(weComMassTaskSendMsg.getLink().getUrl())) {
                    linkAttachmentsMessage.setTitle(weComMassTaskSendMsg.getLink().getTitle());
                    if (StringUtils.isNotBlank(weComMassTaskSendMsg.getLink().getMediaUuid())) {
                        linkAttachmentsMessage.setPicUrl(queryMediaId(weComMassTaskSendMsg.getLink().getMediaUuid()));
                    }
                    if (StringUtils.isNotBlank(weComMassTaskSendMsg.getLink().getDesc())) {
                        linkAttachmentsMessage.setDesc(weComMassTaskSendMsg.getLink().getDesc());
                    }
                    if (StringUtils.isNotBlank(weComMassTaskSendMsg.getLink().getUrl())) {
                        linkAttachmentsMessage.setUrl(weComMassTaskSendMsg.getLink().getUrl());
                    }
                    clientRequest.setLink(linkAttachmentsMessage);
                }
            } else if (weComMassTaskSendMsg.getType() == WeComMassTaskSendMsg.TypeEnum.MINIPROGRAM) {

                WeComSendWelcomeMsgClientRequest.MiniProgramAttachmentsMessage miniProgramAttachmentsMessage =
                        new WeComSendWelcomeMsgClientRequest.MiniProgramAttachmentsMessage();
                if (weComMassTaskSendMsg.getMiniProgram() != null && StringUtils.isNotBlank(weComMassTaskSendMsg.getMiniProgram().getTitle())) {
                    miniProgramAttachmentsMessage.setPicMediaId(queryMediaId(weComMassTaskSendMsg.getMiniProgram().getMediaUuid()));
                    miniProgramAttachmentsMessage.setTitle(weComMassTaskSendMsg.getMiniProgram().getTitle());
                    miniProgramAttachmentsMessage.setAppid(weComMassTaskSendMsg.getMiniProgram().getAppId());
                    miniProgramAttachmentsMessage.setPage(weComMassTaskSendMsg.getMiniProgram().getPage());
                    clientRequest.setMiniprogram(miniProgramAttachmentsMessage);
                }
            }
        });
        RpcResponse<WeComGroupChatWelcomeMsgClientResponse> rpcResponse =
                weComWelcomeMsgRpcService.groupChatWelcomeMsg(clientRequest);
        log.info("group chat welcome msg rpcResponse. rpcResponse={}", rpcResponse);
        if (!rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode())) {
            throw new CommonException(rpcResponse.getCode(), rpcResponse.getMessage());
        }
        if (StringUtils.isEmpty(welcomeMsgGroupChatEntity.getTemplateId())) {
            WeComGroupChatWelcomeMsgClientResponse rpcResponseData = rpcResponse.getData();
            welcomeMsgGroupChatEntity.setTemplateId(rpcResponseData.getTemplateId());
        }
        weComWelcomeMsgGroupChatRepository.save(welcomeMsgGroupChatEntity);
        response.setUuid(welcomeMsgGroupChatEntity.getUuid());
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse checkTitle(String projectId, String corpId, Integer channelId, String name) {
        try {
            log.info("start to check welcome msg customer name. projectUuid={}, corpId={} channelId={}, name={}",
                    projectId, corpId, channelId, name);

            WeComWelcomeMsgGroupChatEntity welcomeMsgGroupChatEntity =
                    weComWelcomeMsgGroupChatRepository.geWelComeMsgByName(projectId, corpId, name);
            // 仅当「非同一个计划且同名」的情况，认为重名
            if (welcomeMsgGroupChatEntity != null && !welcomeMsgGroupChatEntity.getId().equals(channelId)) {
                log.info("failed to check welcome msg customer name. projectUuid={}, corpId={} channelId={}, name={}," +
                                " welcomeMsgGroupChatEntity={}", projectId, corpId, channelId, name,
                        welcomeMsgGroupChatEntity);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getCode()).message(ErrorCodeEnum.ERROR_WECOM_MASS_TASK_DUPLICATE_CNAME.getMessage()).build();
            }
            log.info("succeed to check welcome msg customer name. projectUuid={}, corpId={} channelId={}, name={}",
                    projectId
                    , corpId, channelId, name);
            return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
        } catch (Exception e) {
            log.error("Failed to check welcome msg customer name. projectUuid={}, corpId={} channelId={}, name={}, " +
                    "weComChannelLiveCodeEntity={}", projectId, corpId, channelId, name, e);
        }
        return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getCode()).message(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getMessage()).build();
    }

    @Override
    public BaseResponse getWelcomeMsgDetail(String projectId, String corpId, String uuid) {
        log.info("start to get welcome msg group chat detail. projectUuid={}, corpId={} uuid={}",
                projectId, corpId, uuid);

        WelcomeMsgGroupChatDetailResponse response = new WelcomeMsgGroupChatDetailResponse();
        BaseResponse baseResponse = BaseResponse.success();
        WeComWelcomeMsgGroupChatEntity welcomeMsgGroupChatEntity =
                weComWelcomeMsgGroupChatRepository.queryByCorpAndUuid(corpId, uuid);
        if (welcomeMsgGroupChatEntity == null) {
            return baseResponse;
        }

        BeanUtils.copyProperties(welcomeMsgGroupChatEntity, response);
        response.setCreateTime(DateUtil.formatDateTime(welcomeMsgGroupChatEntity.getCreateTime()));
        response.setWelcomeContent(StringUtils.isBlank(welcomeMsgGroupChatEntity.getWelcomeContent()) ? null :
                JsonUtils.toArray(welcomeMsgGroupChatEntity.getWelcomeContent(),
                        WeComMassTaskSendMsg.class));
        response.setMembers(StringUtils.isBlank(welcomeMsgGroupChatEntity.getMembers()) ? null :
                JsonUtils.toObject(welcomeMsgGroupChatEntity.getMembers(),
                        WelcomeMsgGroupChatDetailResponse.MembersMessage.class));
        baseResponse.setData(response);
        log.info("finish to get welcome msg group chat detail response. projectUuid={}, corpId={} uuid={}, " +
                "baseResponse={}", projectId, corpId, uuid, baseResponse);
        return baseResponse;
    }

    @Override
    public BaseResponse deleteWelcomeMsg(String projectId, String corpId, String uuid) {
        WeComWelcomeMsgGroupChatEntity weComWelcomeMsgGroupChatEntity =
                weComWelcomeMsgGroupChatRepository.queryByCorpAndUuid(corpId, uuid);
        if (weComWelcomeMsgGroupChatEntity == null) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_LIVE_CODE_EXISTS);
        }
        BaseResponse baseResponse = BaseResponse.success();
        WeComDeleteGroupChatWelcomeMsgClientRequest request = new WeComDeleteGroupChatWelcomeMsgClientRequest();
        request.setTemplateId(weComWelcomeMsgGroupChatEntity.getTemplateId());
        WeComAgentMessageEntity agent = agentMessageRepository.getAgentMessageByCorpId(corpId);
        request.setAgentId(agent.getAgentId());
        request.setCorpId(corpId);
        weComWelcomeMsgRpcService.deleteGroupChatWelcomeMsg(request);
        weComWelcomeMsgGroupChatStatisticRepository.deleteByUuid(uuid);
        weComWelcomeMsgGroupChatRepository.deleteByUuid(uuid);

        return baseResponse;
    }

    private String queryMediaId(String uuid) {
        WeComMediaResourceEntity entity = weComMediaResourceRepository.queryByUuid(uuid);
        if (entity == null) return null;
        return entity.getMediaId();
    }
}
