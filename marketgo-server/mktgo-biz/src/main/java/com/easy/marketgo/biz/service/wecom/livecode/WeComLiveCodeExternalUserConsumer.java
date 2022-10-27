package com.easy.marketgo.biz.service.wecom.livecode;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComWelcomeMsgRpcService;
import com.easy.marketgo.biz.service.wecom.tag.MarkCorpTagsProducer;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComContactWayWelcomeType;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComMediaResourceEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeStatisticEntity;
import com.easy.marketgo.core.entity.customer.WeComEventExternalUserEntity;
import com.easy.marketgo.core.model.bo.WeComMarkCorpTags;
import com.easy.marketgo.core.model.bo.WeComSendMassTaskContent;
import com.easy.marketgo.core.model.callback.WeComExternalUserMsg;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeStatisticRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComEventExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.easy.marketgo.common.constants.Constants.AGENT_KEY_FOR_EXTERNALUSER;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-16 20:47:21
 * @description : WeComAddExtContactConsumer.java
 */
@Component
@Slf4j
public class WeComLiveCodeExternalUserConsumer {

    @Autowired
    private WeComChannelLiveCodeRepository weComChannelLiveCodeRepository;

    @Resource
    private WeComWelcomeMsgRpcService welcomeMsgRpcService;

    @Autowired
    private WeComChannelLiveCodeStatisticRepository statisticRepository;

    @Autowired
    private ChannelLiveCodeRefreshService liveCodeRefreshService;

    @Autowired
    private WeComEventExternalUserRepository weComEventExternalUserRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Autowired
    private MarkCorpTagsProducer markCorpTagsProducer;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_LIVE_CODE_EXTERNAL_USER}, containerFactory =
            "weComLiveCodeExternalUserCallbackMsgListenerContainerFactory", concurrency = "1")
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(Message message) {

        String data = new String(message.getBody());
        try {
            log.info("consumer live code external user callback message. message={}", data);
            WeComExternalUserMsg sendData = JsonUtils.toObject(data, WeComExternalUserMsg.class);

            if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_ADD_CUSTOMER) &&
                    StringUtils.isNotBlank(sendData.getState()) &&
                    StringUtils.isNotBlank(sendData.getWelcomeCode())) {

                WeComChannelLiveCodeEntity liveCode =
                        weComChannelLiveCodeRepository.queryByCorpAndSate(sendData.getCorpId(), sendData.getState());
                if (liveCode == null) {
                    log.error("failed to query live code message from db. corpId={}, state={}", sendData.getCorpId(),
                            sendData.getState());
                    return;
                }
                log.info("check welcome type is send welcome message. welcomeType={}", liveCode.getWelcomeType());
                if (liveCode.getWelcomeType().equals(WeComContactWayWelcomeType.USER.getValue())) {
                    doSendWelCodeMsg(sendData.getWelcomeCode(), liveCode);
                }

                log.info("check tag type is set corp tag. tags={}", liveCode.getTags());
                if (StringUtils.isNotBlank(liveCode.getTags())) {
                    List<WeComCorpTag> tags = JsonUtils.toArray(liveCode.getTags(), WeComCorpTag.class);
                    List<String> tagIds = new ArrayList<>();
                    tags.forEach(item -> {
                        tagIds.add(item.getId());
                    });
                    doLiveCodeTagBindTask(sendData, tagIds);
                }
                log.info("add statistic for live code. liveCodeUuid={}", liveCode.getUuid());
                doLiveCodeStaticTask(sendData, liveCode);
                log.info("check add limit status. liveCodeUuid={}", liveCode.getUuid());
                if (liveCode.getAddLimitStatus()) {
                    liveCodeRefreshService.refreshByCallBackMessage(sendData, liveCode.getUuid());
                }

            } else if (sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CUSTOMER) ||
                    sendData.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_FOLLOW_USER)) {

                WeComEventExternalUserEntity entity =
                        weComEventExternalUserRepository.queryByMemberAndExternalUser(sendData.getCorpId(),
                                sendData.getMemberId(), sendData.getExternalUserId(),
                                Constants.WECOM_CALLBACK_MESSAGE_TYPE_ADD_CUSTOMER);
                log.info("query add external user event from db. corpId={}, memberId={}, externalUser={}, entity={}",
                        sendData.getCorpId(), sendData.getMemberId(), sendData.getExternalUserId(), entity);
                if (Objects.isNull(entity) || StringUtils.isBlank(entity.getState())) {
                    return;
                }
                WeComChannelLiveCodeEntity liveCode =
                        weComChannelLiveCodeRepository.queryByCorpAndSate(sendData.getCorpId(), entity.getState());
                log.info("query live code message from db. corpId={}, state={}, liveCode={}", sendData.getCorpId(),
                        entity.getState(), liveCode);
                if (Objects.isNull(liveCode)) {
                    return;
                }
                doLiveCodeStaticTask(sendData, liveCode);
            }

        } catch (Exception e) {
            log.error("failed to consumer external user callback message. message={}", data, e);
        }
    }

    private void doLiveCodeTagBindTask(WeComExternalUserMsg message, List<String> tagIds) {

        WeComMarkCorpTags tags = new WeComMarkCorpTags();
        tags.setCorpId(message.getCorpId());
        tags.setAgentId(AGENT_KEY_FOR_EXTERNALUSER);
        tags.setTagIds(tagIds);
        List<WeComMarkCorpTags.ExternalUserAndMember> list = new ArrayList<>();
        WeComMarkCorpTags.ExternalUserAndMember externalUserAndMember = new WeComMarkCorpTags.ExternalUserAndMember();
        externalUserAndMember.setExternalUserId(message.getExternalUserId());
        externalUserAndMember.setMemberId(message.getMemberId());
        list.add(externalUserAndMember);
        tags.setExternalUsers(list);

        markCorpTagsProducer.markCorpTagForExternalUser(tags);
    }

    private void doLiveCodeStaticTask(WeComExternalUserMsg message, WeComChannelLiveCodeEntity liveCode) {

        Integer dailyIncreasedCount = 0;
        Integer dailyDecreaseCount = 0;
        if (message.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_ADD_CUSTOMER)) {
            dailyIncreasedCount = 1;

        } else if (message.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_CUSTOMER) ||
                message.getChangeType().equals(Constants.WECOM_CALLBACK_MESSAGE_TYPE_DELETE_FOLLOW_USER)) {
            dailyDecreaseCount = 1;
        }

        WeComChannelLiveCodeStatisticEntity statistic =
                statisticRepository.queryByMemberAndEventDate(liveCode.getUuid(), message.getMemberId(),
                        DateUtil.date(message.getCreateTime() * 1000));
        log.info("query to member statistic count from db. member={}, statistic={}", message.getMemberId(), statistic);
        if (Objects.isNull(statistic)) {
            statistic = new WeComChannelLiveCodeStatisticEntity();
            statistic.setCorpId(message.getCorpId());
            statistic.setChannelLiveCodeUuid(liveCode.getUuid());
            statistic.setDailyIncreasedExtUserCount(dailyIncreasedCount);
            statistic.setDailyDecreaseExtUserCount(dailyDecreaseCount);
            statistic.setMemberId(message.getMemberId());
            String name = weComMemberMessageRepository.queryNameByMemberId(message.getCorpId(), message.getMemberId());
            statistic.setMemberName(name);
            statistic.setEventDate(DateUtil.date(message.getCreateTime() * 1000));
            log.info("change increase count to db. statistic={}", statistic);
            statisticRepository.save(statistic);
        } else {
            statistic.setDailyIncreasedExtUserCount(statistic.getDailyIncreasedExtUserCount() + dailyIncreasedCount);
            statistic.setDailyDecreaseExtUserCount(statistic.getDailyDecreaseExtUserCount() + dailyDecreaseCount);
            statistic.setEventDate(Objects.isNull(message.getCreateTime()) ? new Date() :
                    DateTime.of(message.getCreateTime() * 1000).toJdkDate());
            log.info("change decrease count to db. statistic={}", statistic);
            statisticRepository.save(statistic);
        }
    }

    private void doSendWelCodeMsg(String welcomeCode, WeComChannelLiveCodeEntity liveCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String welcomeContent = liveCode.getWelcomeContent();
                log.info("send welcome message. welcomeContent={}", welcomeContent);
                if (StringUtils.isEmpty(welcomeContent)) {
                    log.error("send welcome message is empty.");
                    return;
                }
                List<WeComSendMassTaskContent> contents = JsonUtils.toArray(welcomeContent,
                        WeComSendMassTaskContent.class);
                if (CollectionUtils.isEmpty(contents)) {
                    log.error("parser send welcome message is empty.");
                    return;
                }
                WeComSendWelcomeMsgClientRequest request = new WeComSendWelcomeMsgClientRequest();

                request.setWelcomeCode(welcomeCode);
                List<WeComSendWelcomeMsgClientRequest.AttachmentsMessage> attachments = new ArrayList<>();
                contents.forEach(weComSendMassTaskContent -> {
                    if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.TEXT) {
                        WeComSendWelcomeMsgClientRequest.TextMessage textMessage =
                                new WeComSendWelcomeMsgClientRequest.TextMessage();
                        if (weComSendMassTaskContent.getText() != null && StringUtils.isNotBlank(weComSendMassTaskContent.getText().getContent())) {
                            textMessage.setContent(weComSendMassTaskContent.getText().getContent());
                            request.setText(textMessage);
                        }
                    } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.IMAGE) {
                        WeComSendWelcomeMsgClientRequest.AttachmentsMessage attachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.AttachmentsMessage();
                        WeComSendWelcomeMsgClientRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.ImageAttachmentsMessage();
                        if (StringUtils.isNotBlank(weComSendMassTaskContent.getImage().getMediaUuid())) {
                            imageAttachmentsMessage.setMediaId(queryMediaId(weComSendMassTaskContent.getImage().getMediaUuid()));
                            attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.IMAGE.getValue().toLowerCase());
                            attachmentsMessage.setImage(imageAttachmentsMessage);
                            attachments.add(attachmentsMessage);
                        }
                    } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.LINK) {
                        WeComSendWelcomeMsgClientRequest.AttachmentsMessage attachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.AttachmentsMessage();
                        WeComSendWelcomeMsgClientRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.LinkAttachmentsMessage();
                        if (weComSendMassTaskContent.getLink() != null &&
                                StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getTitle()) &&
                                StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getUrl())) {
                            linkAttachmentsMessage.setTitle(weComSendMassTaskContent.getLink().getTitle());
                            if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getMediaUuid())) {
                                linkAttachmentsMessage.setPicUrl(queryMediaId(weComSendMassTaskContent.getLink().getMediaUuid()));
                            }
                            if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getDesc())) {
                                linkAttachmentsMessage.setDesc(weComSendMassTaskContent.getLink().getDesc());
                            }
                            if (StringUtils.isNotBlank(weComSendMassTaskContent.getLink().getUrl())) {
                                linkAttachmentsMessage.setUrl(weComSendMassTaskContent.getLink().getUrl());
                            }
                            attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.LINK.getValue().toLowerCase());
                            attachmentsMessage.setLink(linkAttachmentsMessage);
                            attachments.add(attachmentsMessage);
                        }
                    } else if (weComSendMassTaskContent.getType() == WeComSendMassTaskContent.TypeEnum.MINIPROGRAM) {
                        WeComSendWelcomeMsgClientRequest.AttachmentsMessage attachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.AttachmentsMessage();
                        WeComSendWelcomeMsgClientRequest.MiniProgramAttachmentsMessage miniProgramAttachmentsMessage =
                                new WeComSendWelcomeMsgClientRequest.MiniProgramAttachmentsMessage();
                        if (weComSendMassTaskContent.getMiniProgram() != null && StringUtils.isNotBlank(weComSendMassTaskContent.getMiniProgram().getTitle())) {
                            miniProgramAttachmentsMessage.setPicMediaId(queryMediaId(weComSendMassTaskContent.getMiniProgram().getMediaUuid()));
                            miniProgramAttachmentsMessage.setTitle(weComSendMassTaskContent.getMiniProgram().getTitle());
                            miniProgramAttachmentsMessage.setAppid(weComSendMassTaskContent.getMiniProgram().getAppId());
                            miniProgramAttachmentsMessage.setPage(weComSendMassTaskContent.getMiniProgram().getPage());
                            attachmentsMessage.setMsgType(WeComSendMassTaskContent.TypeEnum.MINIPROGRAM.getValue().toLowerCase());
                            attachmentsMessage.setMiniprogram(miniProgramAttachmentsMessage);
                            attachments.add(attachmentsMessage);
                        }
                    }
                });

                request.setAttachments(attachments);
                request.setCorpId(liveCode.getCorpId());
                request.setAgentId(liveCode.getAgentId());
                log.info("request to send welcome message. request={}", JsonUtils.toJSONString(request));
                RpcResponse rpcResponse = welcomeMsgRpcService.sendWelcomeMsg(request);
                log.info("send welcome message rpc response. rpcResponse={}", rpcResponse);
            }
        }).start();
    }

    private String queryMediaId(String uuid) {
        WeComMediaResourceEntity entity = weComMediaResourceRepository.queryByUuid(uuid);
        if (entity == null) return null;
        return entity.getMediaId();
    }

    @Data
    public static class WeComCorpTag {
        private String id;

        private String name;
    }
}
