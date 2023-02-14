package com.easy.marketgo.biz.service.wecom.taskcenter;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskSendStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskSendQueueEntity;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.taskcenter.WeComMomentTaskCenterRequest;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSendQueueRepository;
import com.easy.marketgo.core.service.taskcenter.SendTaskCenterBaseConsumer;
import com.easy.marketgo.core.service.taskcenter.TaskCacheManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 9:26 PM
 * Describe:
 */
@Slf4j
@Component
public class SendMomentTaskCenterConsumer extends SendTaskCenterBaseConsumer {

    @Autowired
    private WeComMassTaskSendQueueRepository weComMassTaskSendQueueRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private TaskCacheManagerService taskCacheManagerService;

    @RabbitListener(queues = {RabbitMqConstants.QUEUE_NAME_WECOM_TASK_CENTER_MOMENT}, containerFactory =
            "weComMomentTaskCenterListenerContainerFactory", concurrency = "1")
    public void onMessage(Message message) {
        String data = new String(message.getBody());
        log.info("consumer moment task center message. message={}", data);
        WeComMomentTaskCenterRequest sendData = JsonUtils.toObject(data, WeComMomentTaskCenterRequest.class);
        sendMomentTaskCenter(sendData);
    }

    private void sendMomentTaskCenter(WeComMomentTaskCenterRequest sendData) {

        String taskUuid = sendData.getTaskUuid();
        List<WeComMassTaskSendQueueEntity> entities = weComMassTaskSendQueueRepository.queryByTaskUuid(taskUuid,
                WeComMassTaskSendStatusEnum.UNSEND.name());
        if (CollectionUtils.isEmpty(entities)) {
            log.info("consumer moment task center message, query send queue is empty. taskUuid={}", taskUuid);
            return;
        }
        entities.forEach(entity -> {
            try {
                String tagIds = entity.getExternalUserIds();
                List<String> tagIdList = new ArrayList<>();
                if (StringUtils.isNotBlank(tagIds)) {
                    tagIdList.addAll(Arrays.asList(tagIds.split(",")));
                    sendData.setTagList(tagIdList);
                }
                weComMassTaskSendQueueRepository.updateStatusByTaskUuid(WeComMassTaskSendStatusEnum.UNSEND.name(),
                        WeComMassTaskSendStatusEnum.SEND.name(), taskUuid);

                Integer count = queryExternalUserList(sendData.getProjectUuid(), sendData.getCorpId(),
                        sendData.getUuid(), sendData.getTaskUuid(), sendData.getSender(),
                        sendData.getPlanTime(), tagIdList);
                taskCacheManagerService.setMemberCache(sendData.getCorpId(), sendData.getSender(),
                        taskUuid, sendData.getUuid(), WeComMassTaskExternalUserStatusEnum.UNDELIVERED.getValue());
                sendTaskCenterNotify(sendData.getProjectUuid(), sendData.getCorpId(), sendData.getAgentId(),
                        WeComMassTaskTypeEnum.GROUP,
                        sendData.getUuid(), sendData.getTaskUuid(), sendData.getSender(), sendData.getPlanTime(),
                        sendData.getTaskName(), sendData.getTargetTime(), sendData.getTargetType());
                sendMemberStatusDetail(sendData.getProjectUuid(), sendData.getCorpId(),
                        WeComMassTaskTypeEnum.MOMENT, sendData.getUuid(), taskUuid, sendData.getSender(),
                        sendData.getPlanTime(), "",
                        WeComMassTaskMemberStatusEnum.UNSENT, count, Boolean.TRUE);
                weComMassTaskSendQueueRepository.deleteSendQueueByUuid(entity.getUuid());
            } catch (Exception e) {
                log.error("failed to send moment mass task. entity={}", entity, e);
            }
        });
    }

    private Integer queryExternalUserList(String projectUuid, String corpId, String uuid, String taskUuid,
                                          String memberId, String planTime, List<String> tags) {
        QueryUserGroupBuildSqlParam param =
                QueryUserGroupBuildSqlParam.builder().corpId(corpId).memberIds(Arrays.asList(memberId)).tagRelation(
                        "OR").tags(tags).build();
        List<WeComRelationMemberExternalUserEntity> entities =
                weComRelationMemberExternalUserRepository.listByUserGroupCnd(param);

        log.info("query external user for moment task center. memberId={}, entities={}", memberId, entities);
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(entities)) {
            List<String> externalUserList = new ArrayList<>();
            entities.forEach(entity -> {
                taskCacheManagerService.setCustomerCache(corpId, memberId, taskUuid, uuid,
                        entity.getExternalUserId(), entity.getExternalUserName(), entity.getAvatar());
                externalUserList.add(entity.getExternalUserId());
            });
            List<String> externalUsers = externalUserList.stream().distinct().collect(Collectors.toList());
            count = externalUsers.size();
            sendExternalUserStatusDetail(projectUuid, corpId, WeComMassTaskTypeEnum.GROUP, taskUuid, memberId, uuid,
                    externalUsers, planTime, "", WeComMassTaskExternalUserStatusEnum.UNDELIVERED, Boolean.TRUE);
        }
        return count;
    }
}
