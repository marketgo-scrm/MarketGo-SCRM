package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterMemberEntity;
import com.easy.marketgo.core.model.bo.WeComSendMassTaskContent;
import com.easy.marketgo.core.model.taskcenter.WeComMomentTaskCenterRequest;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/12/22 5:57 PM
 * Describe:
 */
@Slf4j
@Component
public class SendBaseTaskCenterProducer {

    protected static final Integer TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE = 1;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Autowired
    private WeComTaskCenterMemberRepository weComTaskCenterMemberRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    public WeComTaskCenterRequest buildTaskCenterContent(WeComTaskCenterEntity entity) {
        String content = entity.getContent();
        WeComTaskCenterRequest request = new WeComTaskCenterRequest();
        List<WeComSendMassTaskContent> weComSendMassTaskContents = JsonUtils.toArray(content,
                WeComSendMassTaskContent.class);
        request.setChatType(entity.getTaskType());
        request.setMessageType(entity.getMessageType());
        WeComAgentMessageEntity weComAgentMessageEntity =
                weComAgentMessageRepository.getWeComAgentByCorp(entity.getProjectUuid(), entity.getCorpId());
        String agentId = (weComAgentMessageEntity != null ? weComAgentMessageEntity.getAgentId() : "");
        request.setProjectUuid(entity.getProjectUuid());
        request.setTaskUuid(entity.getUuid());
        request.setCorpId(entity.getCorpId());
        request.setAgentId(agentId);
        request.setPlanTime(entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME.getValue()) ?
                DateUtil.formatDateTime(entity.getPlanTime()) :
                DateUtil.formatDateTime(entity.getScheduleTime()));
        if (StringUtils.isNotBlank(entity.getTaskType()) && entity.getTargetTime() != null) {
            request.setTargetType(entity.getTargetType());
            request.setTargetTime(entity.getTargetTime());
        }

        request.setTaskName(entity.getName());
        List<WeComTaskCenterRequest.AttachmentsMessage> attachments = new ArrayList<>();
        weComSendMassTaskContents.forEach(weComSendMassTaskContent -> {
            if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.TEXT) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.TextMessage textMessage = new WeComTaskCenterRequest.TextMessage();
                textMessage.setContent(weComSendMassTaskContent.getText().getContent());
                attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.TEXT.getValue().toLowerCase());
                attachmentsMessage.setText(textMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                        new WeComTaskCenterRequest.ImageAttachmentsMessage();
                if (weComSendMassTaskContent.getImage() != null && StringUtils.isNotBlank(weComSendMassTaskContent.getImage().getMediaUuid())) {
                    imageAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getImage().getMediaUuid()));
                    imageAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getImage().getMediaUuid());
                    imageAttachmentsMessage.setImageContent(weComSendMassTaskContent.getImage().getImageContent());
                    attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.IMAGE.getValue().toLowerCase());
                    attachmentsMessage.setImage(imageAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                        new WeComTaskCenterRequest.LinkAttachmentsMessage();
                if (weComSendMassTaskContent.getLink() != null &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getTitle()) &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getUrl())) {
                    linkAttachmentsMessage.setTitle(weComSendMassTaskContent.getLink().getTitle());
                    if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getDesc())) {
                        linkAttachmentsMessage.setDesc(weComSendMassTaskContent.getLink().getDesc());
                    }
                    if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getUrl())) {
                        linkAttachmentsMessage.setUrl(weComSendMassTaskContent.getLink().getUrl());
                    }
                    if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getMediaUuid())) {
                        linkAttachmentsMessage.setPicUrl(queryMediaId(weComSendMassTaskContent.getLink().getMediaUuid()));
                    }

                    attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.LINK.getValue().toLowerCase());
                    attachmentsMessage.setLink(linkAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.MINIPROGRAM) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.MiniProgramAttachmentsMessage miniProgramAttachmentsMessage =
                        new WeComTaskCenterRequest.MiniProgramAttachmentsMessage();
                if (weComSendMassTaskContent.getMiniProgram() != null &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getMiniProgram().getTitle())) {
                    miniProgramAttachmentsMessage.setTitle(weComSendMassTaskContent.getMiniProgram().getTitle());
                    miniProgramAttachmentsMessage.setAppId(weComSendMassTaskContent.getMiniProgram().getAppId());
                    miniProgramAttachmentsMessage.setPage(weComSendMassTaskContent.getMiniProgram().getPage());
                    miniProgramAttachmentsMessage.setPicMediaId(queryMediaId(weComSendMassTaskContent.getMiniProgram().getMediaUuid()));
                    miniProgramAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getMiniProgram().getMediaUuid());
                    miniProgramAttachmentsMessage.setImageContent(weComSendMassTaskContent.getMiniProgram().getImageContent());
                    attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.MINIPROGRAM.getValue().toLowerCase());
                    attachmentsMessage.setMiniProgram(miniProgramAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.VIDEO) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                        new WeComTaskCenterRequest.VideoAttachmentsMessage();

                videoAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getVideo().getMediaUuid()));
                videoAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getVideo().getMediaUuid());
                videoAttachmentsMessage.setImageContent(weComSendMassTaskContent.getVideo().getImageContent());
                attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.VIDEO.getValue().toLowerCase());
                attachmentsMessage.setVideo(videoAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.FILE) {
                WeComTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComTaskCenterRequest.AttachmentsMessage();
                WeComTaskCenterRequest.FileAttachmentsMessage fileAttachmentsMessage =
                        new WeComTaskCenterRequest.FileAttachmentsMessage();
                fileAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getFile().getMediaUuid()));
                fileAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getFile().getMediaUuid());
                attachmentsMessage.setType(WeComSendMassTaskContent.TypeEnum.FILE.getValue().toLowerCase());
                attachmentsMessage.setFile(fileAttachmentsMessage);
                attachments.add(attachmentsMessage);
            }
        });
        if (CollectionUtils.isNotEmpty(attachments)) {
            request.setAttachments(attachments);
        }

        return request;
    }

    public WeComMomentTaskCenterRequest buildMomentTaskCenterContent(WeComTaskCenterEntity entity) {
        WeComMomentTaskCenterRequest request = new WeComMomentTaskCenterRequest();

        WeComAgentMessageEntity weComAgentMessageEntity =
                weComAgentMessageRepository.getWeComAgentByCorp(entity.getProjectUuid(), entity.getCorpId());
        String agentId = (weComAgentMessageEntity != null ? weComAgentMessageEntity.getAgentId() : "");

        request.setCorpId(entity.getCorpId());
        request.setAgentId(agentId);
        request.setProjectUuid(entity.getProjectUuid());
        request.setTaskName(entity.getName());
        request.setPlanTime(entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME.getValue()) ?
                DateUtil.formatDateTime(entity.getPlanTime()) :
                DateUtil.formatDateTime(entity.getScheduleTime()));
        if (StringUtils.isNotBlank(entity.getTaskType()) && entity.getTargetTime() != null) {
            request.setTargetType(entity.getTargetType());
            request.setTargetTime(entity.getTargetTime());
        }
        List<WeComSendMassTaskContent> weComSendMassTaskContents = JsonUtils.toArray(entity.getContent(),
                WeComSendMassTaskContent.class);
        List<WeComMomentTaskCenterRequest.AttachmentsMessage> attachments = new ArrayList<>();
        weComSendMassTaskContents.forEach(weComSendMassTaskContent -> {
            if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.TEXT) {
                WeComMomentTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentTaskCenterRequest.AttachmentsMessage();
                WeComMomentTaskCenterRequest.TextMessage textAttachmentsMessage =
                        new WeComMomentTaskCenterRequest.TextMessage();
                WeComMomentTaskCenterRequest.TextMessage textMessage =
                        new WeComMomentTaskCenterRequest.TextMessage();
                textMessage.setContent(weComSendMassTaskContent.getText().getContent());
                attachmentsMessage.setText(textAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                WeComMomentTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentTaskCenterRequest.AttachmentsMessage();
                WeComMomentTaskCenterRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                        new WeComMomentTaskCenterRequest.ImageAttachmentsMessage();
                imageAttachmentsMessage.setImageContent(weComSendMassTaskContent.getImage().getImageContent());
                imageAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getImage().getMediaUuid()));
                imageAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getImage().getMediaUuid());
                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.IMAGE.getValue().toLowerCase());
                attachmentsMessage.setImage(imageAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {
                WeComMomentTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentTaskCenterRequest.AttachmentsMessage();
                WeComMomentTaskCenterRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                        new WeComMomentTaskCenterRequest.LinkAttachmentsMessage();
                linkAttachmentsMessage.setTitle(weComSendMassTaskContent.getLink().getTitle());
                linkAttachmentsMessage.setUrl(weComSendMassTaskContent.getLink().getUrl());
                linkAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getLink().getMediaUuid()));
                linkAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getLink().getMediaUuid());
                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.LINK.getValue().toLowerCase());
                attachmentsMessage.setLink(linkAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.VIDEO) {
                WeComMomentTaskCenterRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentTaskCenterRequest.AttachmentsMessage();
                WeComMomentTaskCenterRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                        new WeComMomentTaskCenterRequest.VideoAttachmentsMessage();
                videoAttachmentsMessage.setImageContent(weComSendMassTaskContent.getVideo().getImageContent());
                videoAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getVideo().getMediaUuid()));
                videoAttachmentsMessage.setMediaUuid(weComSendMassTaskContent.getVideo().getMediaUuid());
                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.VIDEO.getValue().toLowerCase());
                attachmentsMessage.setVideo(videoAttachmentsMessage);
                attachments.add(attachmentsMessage);
            }
        });
        if (CollectionUtils.isNotEmpty(attachments)) {
            request.setAttachments(attachments);
        }
        return request;
    }

    private String queryMediaId(String uuid) {
        WeComMediaResourceEntity entity = weComMediaResourceRepository.queryByUuid(uuid);
        return (entity == null) ? null : entity.getMediaId();
    }

    protected String saveMemberTask(WeComTaskCenterEntity entity, String memberId) {
        WeComTaskCenterMemberEntity memberEntity = new WeComTaskCenterMemberEntity();
        String[] IGNORE_ISOLATOR_PROPERTIES = new String[]{"id", "createTime", "updateTime"};
        BeanUtils.copyProperties(entity, memberEntity, IGNORE_ISOLATOR_PROPERTIES);
        memberEntity.setUuid(IdUtil.simpleUUID());
        memberEntity.setMemberId(memberId);
        memberEntity.setTaskType(entity.getTaskType());
        memberEntity.setType("TASK_CENTER");
        memberEntity.setTaskUuid(entity.getUuid());
        memberEntity.setProjectUuid(entity.getProjectUuid());
        memberEntity.setScheduleType(entity.getScheduleType());

        weComTaskCenterMemberRepository.save(memberEntity);
        return memberEntity.getUuid();
    }
}
