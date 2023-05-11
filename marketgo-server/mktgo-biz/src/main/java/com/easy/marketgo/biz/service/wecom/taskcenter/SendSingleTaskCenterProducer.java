package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.enums.*;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.model.taskcenter.WeComTaskCenterRequest;
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

import static com.easy.marketgo.common.constants.RabbitMqConstants.EXCHANGE_NAME_WECOM_TASK_CENTER_SINGLE;
import static com.easy.marketgo.common.constants.RabbitMqConstants.ROUTING_KEY_WECOM_TASK_CENTER_SINGLE;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/12/22 9:25 PM
 * Describe:
 */
@Slf4j
@Component
public class SendSingleTaskCenterProducer extends SendBaseTaskCenterProducer {

    @Autowired
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private RabbitTemplate weComSingleTaskCenterAmqpTemplate;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    public void sendSingleTask() {

        List<WeComTaskCenterEntity> entities =
                weComTaskCenterRepository.getWeComTaskCenterByScheduleTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.SINGLE.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.IMMEDIATE.getValue(),
                                WeComMassTaskScheduleType.FIXED_TIME.getValue()));
        log.info("start to query user group send queue for single task center. entities={}", entities);
        if (CollectionUtils.isNotEmpty(entities)) {
            for (WeComTaskCenterEntity entity : entities) {
                sendSingleTask(entity);
            }
        }

        List<WeComTaskCenterEntity> repeatEntities =
                weComTaskCenterRepository.getWeComTaskCenterByExecuteTime(TASK_CENTER_SEND_USER_GROUP_TIME_BEFORE,
                        WeComMassTaskTypeEnum.SINGLE.name(), WeComMassTaskStatus.COMPUTED.getValue(),
                        Arrays.asList(WeComMassTaskScheduleType.REPEAT_TIME.getValue()));
        log.info("start to query user group send queue for single repeat task center. repeatEntities={}",
                repeatEntities);
        if (CollectionUtils.isNotEmpty(repeatEntities)) {
            for (WeComTaskCenterEntity entity : repeatEntities) {
                sendSingleTask(entity);
            }
        }


    }

    private void sendSingleTask(WeComTaskCenterEntity entity) {
        try {
            if (StringUtils.isBlank(entity.getContent())) {
                throw new CommonException(ErrorCodeEnum.ERROR_BIZ_CONTENT_IS_EMPTY);
            }

            log.info("send content for single task center. content={}", entity.getContent());
            WeComTaskCenterRequest request =
                    buildTaskCenterContent(entity);

            taskCacheManagerService.setCacheContent(entity.getUuid(), JsonUtils.toJSONString(request));
            List<WeComMassTaskSendQueueEntity> weComMassTaskSendQueueEntities =
                    weComMassTaskSendQueueRepository.queryByTaskUuid(entity.getUuid(),
                            WeComMassTaskSendStatusEnum.UNSEND.name());
            log.info("send WeComTaskCenterRequest for single task center. weComMassTaskSendQueueEntities={}",
                    weComMassTaskSendQueueEntities);
            weComTaskCenterRepository.updateTaskStatusByUUID(entity.getUuid(),
                    WeComMassTaskStatus.SENDING.getValue());
            for (WeComMassTaskSendQueueEntity weComMassTaskSendQueueEntity : weComMassTaskSendQueueEntities) {
                request.setSender(weComMassTaskSendQueueEntity.getMemberId());
                log.info("send request to queue for single task center. sendMessage={}",
                        JsonUtils.toJSONString(request));
                String uuid = saveMemberTask(entity, weComMassTaskSendQueueEntity.getMemberId());
                request.setUuid(uuid);
                produceRabbitMqMessage(request);
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
        weComSingleTaskCenterAmqpTemplate.convertAndSend(EXCHANGE_NAME_WECOM_TASK_CENTER_SINGLE,
                ROUTING_KEY_WECOM_TASK_CENTER_SINGLE, values);
    }
}
