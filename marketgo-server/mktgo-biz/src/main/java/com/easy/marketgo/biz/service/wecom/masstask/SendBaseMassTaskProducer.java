package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.WeComMassTaskClientRequest;
import com.easy.marketgo.api.model.request.masstask.WeComMomentMassTaskClientRequest;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.model.bo.WeComSendMassTaskContent;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/3/22 5:57 PM
 * Describe:
 */
@Slf4j
@Component
public class SendBaseMassTaskProducer {

    protected static final Integer SEND_USER_GROUP_TIME_BEFORE = 1;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    public WeComMassTaskClientRequest buildSendMassTaskContent(String type, String content) {
        WeComMassTaskClientRequest request = new WeComMassTaskClientRequest();
        List<WeComSendMassTaskContent> weComSendMassTaskContents = JsonUtils.toArray(content,
                WeComSendMassTaskContent.class);
        request.setChatType(type);
        List<String> mediaUuids = new ArrayList<>();
        List<WeComMassTaskClientRequest.AttachmentsMessage> attachments = new ArrayList<>();
        weComSendMassTaskContents.forEach(weComSendMassTaskContent -> {
            if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.TEXT) {
                WeComMassTaskClientRequest.TextMessage textMessage = new WeComMassTaskClientRequest.TextMessage();
                textMessage.setContent(weComSendMassTaskContent.getText().getContent());
                request.setText(textMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                WeComMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMassTaskClientRequest.AttachmentsMessage();
                WeComMassTaskClientRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                        new WeComMassTaskClientRequest.ImageAttachmentsMessage();
                if (weComSendMassTaskContent.getImage() != null && StringUtils.isNotBlank(weComSendMassTaskContent.getImage().getMediaUuid())) {
                    mediaUuids.add(weComSendMassTaskContent.getImage().getMediaUuid());
                    imageAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getImage().getMediaUuid()));
                    attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.IMAGE.getValue().toLowerCase());
                    attachmentsMessage.setImage(imageAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {
                WeComMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMassTaskClientRequest.AttachmentsMessage();
                WeComMassTaskClientRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                        new WeComMassTaskClientRequest.LinkAttachmentsMessage();
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
                        mediaUuids.add(weComSendMassTaskContent.getLink().getMediaUuid());
                    }

                    attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.LINK.getValue().toLowerCase());
                    attachmentsMessage.setLink(linkAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.MINIPROGRAM) {
                WeComMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMassTaskClientRequest.AttachmentsMessage();
                WeComMassTaskClientRequest.MiniProgramAttachmentsMessage miniProgramAttachmentsMessage =
                        new WeComMassTaskClientRequest.MiniProgramAttachmentsMessage();
                if (weComSendMassTaskContent.getMiniProgram() != null &&
                        StringUtils.isNotBlank(weComSendMassTaskContent.getMiniProgram().getTitle())) {
                    miniProgramAttachmentsMessage.setTitle(weComSendMassTaskContent.getMiniProgram().getTitle());
                    miniProgramAttachmentsMessage.setAppId(weComSendMassTaskContent.getMiniProgram().getAppId());
                    miniProgramAttachmentsMessage.setPage(weComSendMassTaskContent.getMiniProgram().getPage());
                    mediaUuids.add(weComSendMassTaskContent.getMiniProgram().getMediaUuid());
                    miniProgramAttachmentsMessage.setPicMediaId(queryMediaId(weComSendMassTaskContent.getMiniProgram().getMediaUuid()));
                    attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.MINIPROGRAM.getValue().toLowerCase());
                    attachmentsMessage.setMiniProgram(miniProgramAttachmentsMessage);
                    attachments.add(attachmentsMessage);
                }
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.VIDEO) {
                WeComMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMassTaskClientRequest.AttachmentsMessage();
                WeComMassTaskClientRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                        new WeComMassTaskClientRequest.VideoAttachmentsMessage();

                mediaUuids.add(weComSendMassTaskContent.getVideo().getMediaUuid());
                videoAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getVideo().getMediaUuid()));

                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.VIDEO.getValue().toLowerCase());
                attachmentsMessage.setVideo(videoAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.FILE) {
                WeComMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMassTaskClientRequest.AttachmentsMessage();
                WeComMassTaskClientRequest.FileAttachmentsMessage fileAttachmentsMessage =
                        new WeComMassTaskClientRequest.FileAttachmentsMessage();
                mediaUuids.add(weComSendMassTaskContent.getFile().getMediaUuid());
                fileAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getFile().getMediaUuid()));

                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.FILE.getValue().toLowerCase());
                attachmentsMessage.setFile(fileAttachmentsMessage);
                attachments.add(attachmentsMessage);
            }
        });
        if (CollectionUtils.isNotEmpty(attachments)) {
            request.setAttachments(attachments);
        }
        if (CollectionUtils.isNotEmpty(mediaUuids)) {
            finishUpdateMediaId(mediaUuids);
        }
        return request;
    }

    public WeComMomentMassTaskClientRequest buildSendMomentMassTaskContent(String content) {
        WeComMomentMassTaskClientRequest request = new WeComMomentMassTaskClientRequest();
        List<WeComSendMassTaskContent> weComSendMassTaskContents = JsonUtils.toArray(content,
                WeComSendMassTaskContent.class);
        List<String> mediaUuids = new ArrayList<>();
        List<WeComMomentMassTaskClientRequest.AttachmentsMessage> attachments = new ArrayList<>();
        weComSendMassTaskContents.forEach(weComSendMassTaskContent -> {
            if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.TEXT) {
                WeComMomentMassTaskClientRequest.TextMessage textMessage =
                        new WeComMomentMassTaskClientRequest.TextMessage();
                textMessage.setContent(weComSendMassTaskContent.getText().getContent());
                request.setText(textMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                WeComMomentMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentMassTaskClientRequest.AttachmentsMessage();
                WeComMomentMassTaskClientRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                        new WeComMomentMassTaskClientRequest.ImageAttachmentsMessage();
                mediaUuids.add(weComSendMassTaskContent.getImage().getMediaUuid());
                imageAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getImage().getMediaUuid()));
                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.IMAGE.getValue().toLowerCase());
                attachmentsMessage.setImage(imageAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {
                WeComMomentMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentMassTaskClientRequest.AttachmentsMessage();
                WeComMomentMassTaskClientRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                        new WeComMomentMassTaskClientRequest.LinkAttachmentsMessage();
                linkAttachmentsMessage.setTitle(weComSendMassTaskContent.getLink().getTitle());
                linkAttachmentsMessage.setMediaId(weComSendMassTaskContent.getLink().getUrl());
                mediaUuids.add(weComSendMassTaskContent.getLink().getMediaUuid());
                linkAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getLink().getMediaUuid()));

                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.LINK.getValue().toLowerCase());
                attachmentsMessage.setLink(linkAttachmentsMessage);
                attachments.add(attachmentsMessage);
            } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.VIDEO) {
                WeComMomentMassTaskClientRequest.AttachmentsMessage attachmentsMessage =
                        new WeComMomentMassTaskClientRequest.AttachmentsMessage();
                WeComMomentMassTaskClientRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                        new WeComMomentMassTaskClientRequest.VideoAttachmentsMessage();
                mediaUuids.add(weComSendMassTaskContent.getVideo().getMediaUuid());
                videoAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getVideo().getMediaUuid()));

                attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.VIDEO.getValue().toLowerCase());
                attachmentsMessage.setVideo(videoAttachmentsMessage);
                attachments.add(attachmentsMessage);
            }
        });
        if (CollectionUtils.isNotEmpty(attachments)) {
            request.setAttachments(attachments);
        }
        if (CollectionUtils.isNotEmpty(mediaUuids)) {
            finishUpdateMediaId(mediaUuids);
        }
        return request;
    }

    private String queryMediaId(String uuid) {
        WeComMediaResourceEntity entity = weComMediaResourceRepository.queryByUuid(uuid);
        if (entity == null) return null;
        return entity.getMediaId();
    }

    private void finishUpdateMediaId(List<String> mediaUuids) {
        weComMediaResourceRepository.updateMediaByUuid(mediaUuids);
    }
}
