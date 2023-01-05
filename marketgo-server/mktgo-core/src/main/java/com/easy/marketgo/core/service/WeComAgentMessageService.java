package com.easy.marketgo.core.service;

import com.easy.marketgo.api.model.request.WeComSendAgentMessageClientRequest;
import com.easy.marketgo.api.service.WeComSendAgentMessageRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.enums.WeComTaskCenterTargetTypeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.ProjectConfigEntity;
import com.easy.marketgo.core.entity.TenantConfigEntity;
import com.easy.marketgo.core.repository.wecom.ProjectConfigRepository;
import com.easy.marketgo.core.repository.wecom.TenantConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private ProjectConfigRepository projectConfigRepository;

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

    public void sendTaskCenterMessage(String projectId, String taskType, String corpId, String agentId,
                                      String taskUuid, String uuid, String taskName, List<String> members,
                                      String planTime, Integer targetTime, String targetType) {
        WeComSendAgentMessageClientRequest appMsgRequest = new WeComSendAgentMessageClientRequest();

        appMsgRequest.setAgentId(agentId);
        appMsgRequest.setCorpId(corpId);
        appMsgRequest.setMsgType(WeComSendAgentMessageClientRequest.MsgTypeEnum.TEXTCARD);
        appMsgRequest.setMsgId(taskUuid);

        ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
        if (projectConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
        }

        TenantConfigEntity tenantConfigEntity =
                tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
        if (tenantConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
        }

        String target = targetTime + WeComTaskCenterTargetTypeEnum.fromValue(targetType).getCname();
        String desc = String.format("<div class=\\\"gray\\\">%s</div> <div class=\\\"normal\\\">%s</div><div " +
                "class=\\\"highlight\\\">请在收到任务后%s完成</div>", planTime, taskName, target);
        String url = String.format("%s/mktgo/client/wecom/task_center/detail?member_id=%s&task_uuid=%s%s",
                tenantConfigEntity.getServerAddress(), members.get(0), taskUuid, StringUtils.isNotBlank(uuid) ?
                        "&uuid=" + uuid : "");

        Map<String, String> textMessage = new HashMap<>();
        textMessage.put("title", String.format("%s任务通知", WeComMassTaskTypeEnum.fromValue(taskType).getCname()));
        textMessage.put("description", desc);
        textMessage.put("url", url);
        textMessage.put("btntxt", "任务详情");
        appMsgRequest.setContent(JsonUtils.toJSONString(textMessage));
        appMsgRequest.setToUser(members);
        log.info("send text card massage to member for task center. request={}", appMsgRequest);
        weComSendAgentMessageRpcService.sendAgentMessage(appMsgRequest);
    }
}
