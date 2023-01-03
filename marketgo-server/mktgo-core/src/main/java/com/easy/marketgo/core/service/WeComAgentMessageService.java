package com.easy.marketgo.core.service;

import com.easy.marketgo.api.model.request.WeComSendAgentMessageClientRequest;
import com.easy.marketgo.api.service.WeComSendAgentMessageRpcService;
import com.easy.marketgo.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/30/22 12:27 PM
 * Describe:
 */

@Slf4j
@Service
public class WeComAgentMessageService {

    @Resource
    private WeComSendAgentMessageRpcService weComSendAgentMessageRpcService;

    public void sendRemindMessage(String taskType, String corpId, String agentId, String taskUuid,
                                  List<String> members) {
        WeComSendAgentMessageClientRequest appMsgRequest = new WeComSendAgentMessageClientRequest();

        appMsgRequest.setAgentId(agentId);
        appMsgRequest.setCorpId(corpId);
        appMsgRequest.setMsgType(WeComSendAgentMessageClientRequest.MsgTypeEnum.TEXT);
        appMsgRequest.setMsgId(taskUuid);
        Map<String, String> textMessage = new HashMap<>();
        textMessage.put("content", "【任务提醒】有新的员工任务啦！\n"
                + "记得及时完成哦～");
        appMsgRequest.setContent(JsonUtils.toJSONString(textMessage));
        appMsgRequest.setToUser(members);
        log.info("send text massage to member for remind. request={}", appMsgRequest);
        weComSendAgentMessageRpcService.sendAgentMessage(appMsgRequest);
    }
}
