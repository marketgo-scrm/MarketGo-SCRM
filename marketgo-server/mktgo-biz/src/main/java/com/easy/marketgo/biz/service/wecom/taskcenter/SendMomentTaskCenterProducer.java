package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.taskcenter.WeComMomentTaskCenterRequest;
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

import static com.easy.marketgo.common.constants.RabbitMqConstants.EXCHANGE_NAME_WECOM_TASK_CENTER_MOMENT;
import static com.easy.marketgo.common.constants.RabbitMqConstants.ROUTING_KEY_WECOM_TASK_CENTER_MOMENT;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 9:27 PM
 * Describe:
 */
@Slf4j
@Component
public class SendMomentTaskCenterProducer extends SendBaseTaskCenterProducer {

    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private RabbitTemplate weComMomentTaskCenterAmqpTemplate;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    public void sendMomentTask() {
        log.info("start query send moment task center");

        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComTaskCenterByScheduleTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.MOMENT.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.IMMEDIATE.getValue(),
                                WeComMassTaskScheduleType.FIXED_TIME.getValue()));
        log.info("start query user group send queue for moment task center. entities={}", entities);
        if (CollectionUtils.isNotEmpty(entities)) {
            for (WeComTaskCenterEntity entity : entities) {
                sendMomentTaskCenter(entity);
            }
        }

        List<WeComTaskCenterEntity> repeatEntities =
                weComTaskCenterRepository.getWeComTaskCenterByExecuteTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.MOMENT.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.REPEAT_TIME.getValue()));
        log.info("start to query user group send queue for moment repeat task center. repeatEntities={}",
                repeatEntities);
        if (CollectionUtils.isNotEmpty(repeatEntities)) {
            for (WeComTaskCenterEntity entity : repeatEntities) {
                sendMomentTaskCenter(entity);
            }
        }
    }

    private void sendMomentTaskCenter(WeComTaskCenterEntity entity) {
        try {
            log.info("query send moment task center content. content={}", entity.getContent());
            if (StringUtils.isBlank(entity.getContent())) {
                throw new CommonException(ErrorCodeEnum.ERROR_BIZ_CONTENT_IS_EMPTY);
            }
            WeComMomentTaskCenterRequest request = buildMomentTaskCenterContent(entity);

            taskCacheManagerService.setCacheContent(entity.getUuid(), JsonUtils.toJSONString(request));
            List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities =
                    weComMassTaskSendQueueRepository.queryByTaskUuid(entity.getUuid(),
                            WeComMassTaskSendStatusEnum.UNSEND.name());
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SENDING.getValue());
            for (WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity : weComMassTaskSendQueueEntities) {
                request.setTaskUuid(weComMassTaskSendQueueEntity.getTaskUuid());
                List<String> sendList = Arrays.asList(weComMassTaskSendQueueEntity.getMemberId().split(","));
                for (String sender : sendList) {
                    request.setSender(sender);
                    log.info("send request to queue for single task center. sendMessage={}",
                            JsonUtils.toJSONString(request));
                    String uuid = saveMemberTask(entity, sender);
                    request.setUuid(uuid);
                    produceRabbitMqMessage(request);
                }

                log.info("send moment task center to queue message. request={}", JsonUtils.toJSONString(request));
            }
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SENT.getValue());
        } catch (Exception e) {
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SEND_FAILED.getValue());
            log.error("failed to send single task center message to queue.", e);
        }
    }

    private void produceRabbitMqMessage(Object values) {
        weComMomentTaskCenterAmqpTemplate.convertAndSend(EXCHANGE_NAME_WECOM_TASK_CENTER_MOMENT,
                ROUTING_KEY_WECOM_TASK_CENTER_MOMENT, values);
    }
}
