package com.easy.marketgo.biz.service.wecom.masstask;

import com.easy.marketgo.api.model.request.masstask.WeComMomentMassTaskClientRequest;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.easy.marketgo.common.constants.RabbitMqConstants.*;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 9:27 PM
 * Describe:
 */
@Slf4j
@Component
public class SendMomentMassTaskProducer extends SendBaseMassTaskProducer {

    @Autowired
    private WeComMassTaskRepository weComMassTaskRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private RabbitTemplate weComMomentMassTaskAmqpTemplate;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    public void sendMomentMassTask() {
        log.info("start query send moment mass task");
        List<WeComMassTaskEntity> entities =
                weComMassTaskRepository.getWeComMassTaskByScheduleTime(SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.MOMENT.name(), WeComMassTaskStatus.COMPUTED.getValue());
        if (CollectionUtils.isEmpty(entities)) {
            log.info("query single mass task is empty.");
            return;
        }

        for (WeComMassTaskEntity entity : entities) {
            try {
                WeComAgentMessageEntity agentMessageEntity =
                        weComAgentMessageRepository.getWeComAgentByCorp(entity.getProjectUuid(), entity.getCorpId());
                log.info("query agent message for mass task. agentMessageEntity={}", agentMessageEntity);
                String agentId = (agentMessageEntity == null) ? "" : agentMessageEntity.getAgentId();

                String content = entity.getContent();
                log.info("query send moment mass task content. content={}", content);
                if (StringUtils.isNotBlank(content)) {
                    WeComMomentMassTaskClientRequest request = buildSendMomentMassTaskContent(content);
                    request.setCorpId(entity.getCorpId());
                    request.setAgentId(agentId);
                    request.setProjectUuid(entity.getProjectUuid());
                    List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities =
                            weComMassTaskSendQueueRepository.queryByTaskUuid(entity.getUuid(),
                                    WeComMassTaskSendStatusEnum.UNSEND.name());
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.SENDING.getValue());
                    for (WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity : weComMassTaskSendQueueEntities) {
                        WeComMomentMassTaskClientRequest.VisibleRangeMessage visibleRangeMessage =
                                new WeComMomentMassTaskClientRequest.VisibleRangeMessage();
                        WeComMomentMassTaskClientRequest.SenderListMessage senderListMessage =
                                new WeComMomentMassTaskClientRequest.SenderListMessage();
                        List<String> sendList = Arrays.asList(weComMassTaskSendQueueEntity.getMemberId().split(","));
                        request.setTaskUuid(weComMassTaskSendQueueEntity.getTaskUuid());
                        senderListMessage.setUserList(sendList);
                        visibleRangeMessage.setSenderList(senderListMessage);
                        request.setVisibleRange(visibleRangeMessage);
                        log.info("send moment mass task to queue message. request={}", JsonUtils.toJSONString(request));
                        produceRabbitMqMessage(request);
                    }
                    weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                            WeComMassTaskStatus.SENT.getValue());
                }
            } catch (Exception e) {
                weComMassTaskRepository.updateTaskStatusByUUID(entity.getUuid(),
                        WeComMassTaskStatus.SEND_FAILED.getValue());
                log.error("failed to send single mass task message to queue.", e);
            }
        }
    }

    private void produceRabbitMqMessage(Object values) {
        weComMomentMassTaskAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_WECOM_MASS_TASK_MOMENT,
                ROUTING_KEY_WECOM_MASS_TASK_MOMENT, values);
    }

}
