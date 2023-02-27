package com.easy.marketgo.biz.service.wecom.livecode;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.easy.marketgo.api.model.request.WeComSendWelcomeMsgClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComEditExternalUserCorpTagClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComExternalUserRpcService;
import com.easy.marketgo.api.service.WeComWelcomeMsgRpcService;
import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeStatisticEntity;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeStatisticRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-16 20:47:21
 * @description : WeComAddExtContactConsumer.java
 */
@Component
@Log4j2
public class WeComAddExtContactConsumer {

    @Autowired
    private WeComChannelLiveCodeRepository weComChannelLiveCodeRepository;
    @Autowired
    private WeComWelcomeMsgRpcService welcomeMsgRpcService;
    @Autowired
    private WeComChannelLiveCodeStatisticRepository statisticRepository;
    @Autowired
    private ChannelLiveCodeRefreshService liveCodeRefreshService;
    @Autowired
    private WeComExternalUserRpcService weComExternalUserRpcService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_ADD_EXT_CONTACT}, containerFactory =
            "weComChangeExtContactListenerContainerFactory", concurrency = "1")
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(Message message) {

        String data = new String(message.getBody());

        try {
            log.info(" message={}", data);
            WeComXmlMessage weComXmlMessage = JsonUtils.toObject(data, WeComXmlMessage.class);
            if (StringUtils.isNotEmpty(weComXmlMessage.getState())) {
                log.info("渠道活码任务处理{}", weComXmlMessage);

                doLiveStaticTask(weComXmlMessage);
                doLiveCodeTask(weComXmlMessage);
                doLiveCodeTagBindTask(weComXmlMessage);
                doLiveCodeRefreshTask(weComXmlMessage);

            } else {

            }


        } catch (Exception e) {
            log.error("failed to consumer message. message={}", data, e);
        }
    }

    private void doLiveCodeTagBindTask(WeComXmlMessage weComXmlMessage) {
        String corpId = weComXmlMessage.getToUserName();
        String userId = weComXmlMessage.getUserID();
        String externalUserId = weComXmlMessage.getExternalUserId();
        WeComChannelLiveCodeEntity liveCode = weComChannelLiveCodeRepository.queryByCorpAndSate(corpId, weComXmlMessage.getState());
        if (StringUtils.isEmpty(liveCode.getTags())){
            return;
        }

        JSONArray array = JSONUtil.parseArray(liveCode.getTags());
        List<WeComUserGroupAudienceRule.WeComCorpTag> tags =
                array.stream().map(o -> JSONUtil.toBean(JSONUtil.parseObj(o), WeComUserGroupAudienceRule.WeComCorpTag.class)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        WeComEditExternalUserCorpTagClientRequest request = new WeComEditExternalUserCorpTagClientRequest();
        request.setUserId(userId);
        request.setExternalUserId(externalUserId);
        request.setAddTag(tags.stream().map(WeComUserGroupAudienceRule.WeComCorpTag::getId).collect(Collectors.toList()) );
        request.setCorpId(corpId);
        request.setAgentId(liveCode.getAgentId());
        weComExternalUserRpcService.editExternalUserCorpTag(request);
        log.info("客户打标签结束：{}",weComXmlMessage);

    }

    private void doLiveCodeRefreshTask(WeComXmlMessage weComXmlMessage) {

//        liveCodeRefreshService.refreshByCallBackMessage(weComXmlMessage);

    }

    private void doLiveStaticTask(WeComXmlMessage weComXmlMessage) {

        String corpId = weComXmlMessage.getToUserName();
        WeComChannelLiveCodeEntity liveCode = weComChannelLiveCodeRepository.queryByCorpAndSate(corpId, weComXmlMessage.getState());

        String userId = weComXmlMessage.getUserID();
        WeComChannelLiveCodeStatisticEntity statistic = statisticRepository.queryByMemberAndEventDate(liveCode.getUuid(), userId, DateTime
                .of(weComXmlMessage.getCreateTime() * 1000)
                .toJdkDate());
        if (Objects.isNull(statistic)) {
            statistic = new WeComChannelLiveCodeStatisticEntity();
            statistic.setCorpId(corpId);
            statistic.setChannelLiveCodeUuid(liveCode.getUuid());
            statistic.setDailyIncreasedExtUserCount(1);
            statistic.setDailyDecreaseExtUserCount(0);
            statistic.setMemberId(userId);
            statistic.setEventDate(DateTime
                    .of(weComXmlMessage.getCreateTime() * 1000)
                    .toJdkDate());
            statisticRepository.save(statistic);
        } else {
            statistic.setDailyIncreasedExtUserCount(statistic.getDailyDecreaseExtUserCount() + 1);
            statistic.setEventDate(
                    Objects.isNull(weComXmlMessage.getCreateTime()) ? new Date() :
                            DateTime
                                    .of(weComXmlMessage.getCreateTime() * 1000)
                                    .toJdkDate());
            statisticRepository.save(statistic);
        }
    }


    private void doLiveCodeTask(WeComXmlMessage weComXmlMessage) {

        String corpId = weComXmlMessage.getToUserName();
        WeComChannelLiveCodeEntity liveCode = weComChannelLiveCodeRepository.queryByCorpAndSate(corpId, weComXmlMessage.getState());
        if (Objects.nonNull(liveCode)) {
            doSendWelCodeMsg(weComXmlMessage, liveCode);

        }

    }

    private void doSendWelCodeMsg(WeComXmlMessage weComXmlMessage, WeComChannelLiveCodeEntity liveCode) {

        if (liveCode.getWelcomeType() != 0) {
            String externalUserId = weComXmlMessage.getExternalUserId();
            String welcomeCode = weComXmlMessage.getWelcomeCode();
            if (StringUtils.isNotEmpty(externalUserId) && StringUtils.isNotEmpty(welcomeCode)) {
                log.info("发送欢迎语{} {}", weComXmlMessage, liveCode);
                sendWelcomeMsg(weComXmlMessage, liveCode);
            }

        }
    }

    private void sendWelcomeMsg(WeComXmlMessage weComXmlMessage, WeComChannelLiveCodeEntity liveCode) {


        String welcomeContent = liveCode.getWelcomeContent();
        log.info("发送欢迎语任务处理开始{}", welcomeContent);
        if (StringUtils.isEmpty(welcomeContent)) {
            return;
        }
        JSONArray jsonArray = JSONUtil.parseArray(welcomeContent);
        JSONObject o = JSONUtil.parseObj(jsonArray.get(0));
        WeComSendWelcomeMsgClientRequest request = new WeComSendWelcomeMsgClientRequest();
        request.setWelcomeCode(weComXmlMessage.getWelcomeCode());
        request.setText(JSONUtil.toBean(o.getJSONObject("text"), WeComSendWelcomeMsgClientRequest.TextMessage.class));
        TypeReference<List<WeComSendWelcomeMsgClientRequest.AttachmentsMessage>> list =
                new TypeReference<List<WeComSendWelcomeMsgClientRequest.AttachmentsMessage>>() {};
        request.setAttachments(JSONUtil.toBean(o, list, true));
        request.setCorpId(liveCode.getCorpId());
        request.setAgentId(liveCode.getAgentId());
        RpcResponse rpcResponse = welcomeMsgRpcService.sendWelcomeMsg(request);

        if (!rpcResponse.getSuccess()) {
            throw new CommonException("发送渠道欢迎语失败");
        }
    }

}
