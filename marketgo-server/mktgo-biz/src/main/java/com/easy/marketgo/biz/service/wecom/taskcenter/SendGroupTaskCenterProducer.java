package com.easy.marketgo.biz.service.wecom.taskcenter;

import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.easy.marketgo.common.constants.RabbitMqConstants.EXCHANGE_NAME_WECOM_TASK_CENTER_GROUP;
import static com.easy.marketgo.common.constants.RabbitMqConstants.ROUTING_KEY_WECOM_TASK_CENTER_GROUP;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 9:25 PM
 * Describe:
 */
@Slf4j
@Component
public class SendGroupTaskCenterProducer extends SendBaseTaskCenterProducer {
    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private RabbitTemplate weComGroupTaskCenterAmqpTemplate;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    public void sendGroupTask() {
        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComTaskCenterByScheduleTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.GROUP.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.IMMEDIATE.getValue(),
                                WeComMassTaskScheduleType.FIXED_TIME.getValue()));
        log.info("start query user group send queue for group task center. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            for (WeComTaskCenterEntity entity : entities) {
                sendGroupTaskCenter(entity);
            }
        }

        List<WeComTaskCenterEntity> repeatEntities =
                weComTaskCenterRepository.getWeComTaskCenterByExecuteTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.GROUP.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.REPEAT_TIME.getValue()));
        log.info("start to query user group send queue for group repeat task center. repeatEntities={}",
                repeatEntities);
        if (CollectionUtils.isNotEmpty(repeatEntities)) {
            for (WeComTaskCenterEntity entity : repeatEntities) {
                sendGroupTaskCenter(entity);
            }
        }
    }

    private void sendGroupTaskCenter(WeComTaskCenterEntity entity) {
        log.info("send group task center. entity={}", entity);
        WeComAgentMessageEntity weComAgentMessageEntity =
                weComAgentMessageRepository.getWeComAgentByCorp(entity.getProjectUuid(), entity.getCorpId());
        String agentId = (weComAgentMessageEntity == null) ? "" : weComAgentMessageEntity.getAgentId();
        String content = entity.getContent();
        if (StringUtils.isNotBlank(content)) {
            WeComTaskCenterRequest request =
                    buildTaskCenterContent(WeComMassTaskTypeEnum.GROUP.name().toLowerCase(),
                            entity.getMessageType(), content);
            request.setProjectUuid(entity.getProjectUuid());
            request.setTaskUuid(entity.getUuid());
            request.setCorpId(entity.getCorpId());
            request.setAgentId(agentId);
            request.setPlanTime(entity.getScheduleType().equals(WeComMassTaskScheduleType.REPEAT_TIME) ?
                    DateUtil.formatDateTime(entity.getPlanTime()) :
                    DateUtil.formatDateTime(entity.getScheduleTime()));
            if (StringUtils.isNotBlank(entity.getTaskType()) && entity.getTargetTime() != null) {
                request.setTargetType(entity.getTargetType());
                request.setTargetTime(entity.getTargetTime());
            }
            taskCacheManagerService.setCacheContent(entity.getUuid(), JsonUtils.toJSONString(request));
            List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities =
                    weComMassTaskSendQueueRepository.queryByTaskUuid(entity.getUuid(),
                            WeComMassTaskSendStatusEnum.UNSEND.name());
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SENDING.getValue());
            for (WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity : weComMassTaskSendQueueEntities) {
                request.setSender(weComMassTaskSendQueueEntity.getMemberId());

                log.info("send request to queue for group task center. sendMessage={}",
                        JsonUtils.toJSONString(request));
                String uuid = saveMemberTask(entity, weComMassTaskSendQueueEntity.getMemberId());
                request.setUuid(uuid);
                produceRabbitMqMessage(request);
            }
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SENT.getValue());
        }
    }

    private void produceRabbitMqMessage(Object values) {
        weComGroupTaskCenterAmqpTemplate.convertAndSend(EXCHANGE_NAME_WECOM_TASK_CENTER_GROUP,
                ROUTING_KEY_WECOM_TASK_CENTER_GROUP, values);
    }
}
