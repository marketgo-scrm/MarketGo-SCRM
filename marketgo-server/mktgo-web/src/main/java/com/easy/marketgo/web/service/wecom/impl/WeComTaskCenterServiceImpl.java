package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.api.service.WeComSendAgentMessageRpcService;
import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.masstask.WeComMassTaskEntity;
import com.easy.marketgo.core.entity.taskcenter.WeComTaskCenterEntity;
import com.easy.marketgo.core.repository.media.WeComMediaResourceRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskRepository;
import com.easy.marketgo.core.repository.wecom.masstask.WeComMassTaskSyncStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterExternalUserStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterMemberStatisticRepository;
import com.easy.marketgo.core.repository.wecom.taskcenter.WeComTaskCenterRepository;
import com.easy.marketgo.web.model.request.WeComTaskCenterRequest;
import com.easy.marketgo.web.model.response.BaseResponse;
import com.easy.marketgo.web.service.wecom.WeComTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/12/22 5:38 PM
 * Describe:
 */

@Slf4j
@Service
public class WeComTaskCenterServiceImpl implements WeComTaskCenterService {

    @Resource
    private WeComTaskCenterRepository weComTaskCenterRepository;

    @Resource
    private WeComTaskCenterMemberStatisticRepository weComTaskCenterMemberStatisticRepository;

    @Resource
    private WeComTaskCenterExternalUserStatisticRepository weComTaskCenterExternalUserStatisticRepository;

    @Autowired
    private WeComMediaResourceRepository weComMediaResourceRepository;

    @Resource
    private WeComSendAgentMessageRpcService weComSendAgentMessageRpcService;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;

    @Override
    public BaseResponse updateOrInsertTaskCenter(String projectId, String corpId, String taskType,
                                                 WeComTaskCenterRequest weComTaskCenterRequest) {
        log.info("update or save weCom task center. projectId={}, corpId={}, taskType={}, " +
                "weComTaskCenterRequest={}", projectId, corpId, taskType, weComTaskCenterRequest);
        WeComTaskCenterEntity entity = new WeComTaskCenterEntity();
        if (weComTaskCenterRequest.getId() != null) {
            entity = weComTaskCenterRepository.queryById(weComTaskCenterRequest.getId());
            String taskStatus = entity.getTaskStatus();
            if (!taskStatus.equalsIgnoreCase(WeComMassTaskStatus.UNSTART.getValue())) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_NOT_SUPPORT_CHANGE);
            }
        }

        BeanUtils.copyProperties(weComTaskCenterRequest, entity);
        entity.setUuid(IdUtil.simpleUUID());
        entity.setScheduleType(weComTaskCenterRequest.getScheduleType().getValue());
        entity.setTaskStatus(WeComMassTaskStatus.UNSTART.getValue());
        if (StringUtils.isNotEmpty(weComTaskCenterRequest.getScheduleTime())) {
            entity.setScheduleTime(DateUtil.parse(weComTaskCenterRequest.getScheduleTime()));
        } else {
            entity.setScheduleTime(DateUtil.date());
        }

        if (weComTaskCenterRequest.getScheduleType() == WeComMassTaskScheduleType.REPEAT_TIME) {
            if (StringUtils.isBlank(weComTaskCenterRequest.getRepeatEndTime()) ||
                    StringUtils.isBlank(weComTaskCenterRequest.getRepeatStartTime()) ||
                    weComTaskCenterRequest.getRepeatType() == null) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
            }

            entity.setRepeatType(weComTaskCenterRequest.getRepeatType().getValue());
            entity.setRepeatStartTime(DateUtil.parse(weComTaskCenterRequest.getRepeatStartTime()));
            entity.setRepeatStartTime(DateUtil.parse(weComTaskCenterRequest.getRepeatEndTime()));
        }
        entity.setContent(JsonUtils.toJSONString(weComTaskCenterRequest.getContent()));

        entity.setCorpId(corpId);
        entity.setProjectUuid(projectId);
        entity.setTaskType(taskType);

        log.info("save weCom task center. entity={}", JsonUtils.toJSONString(entity));
        weComTaskCenterRepository.save(entity);

        if (weComTaskCenterRequest.getScheduleType() == WeComMassTaskScheduleType.IMMEDIATE) {
            log.info("notify compute user group for weCom task center. entity={}", entity);
            xxlJobManualTriggerService.manualTriggerHandler("computeUserGroupDetail");
        }
        return BaseResponse.success();
    }

    @Override
    public BaseResponse remindSendTask(String projectId, String corpId, String taskType, String taskUuid) {
        return null;
    }

    @Override
    public BaseResponse listTaskCenter(String projectId, String taskType, Integer pageNum, Integer pageSize,
                                       String corpId, List<String> statuses, String keyword,
                                       List<String> createUserIds, String sortKey, String sortOrder, String startTime
            , String endTime) {
        return null;
    }

    @Override
    public BaseResponse listMembers(String projectId, String corpId, String taskType, String metricsType,
                                    Integer pageNum, Integer pageSize, String taskUuid, String keyword, String status) {
        return null;
    }

    @Override
    public BaseResponse getTaskCenterDetails(String projectUuid, Integer taskId) {
        return null;
    }

    @Override
    public BaseResponse getTaskCenterStatistic(String taskUuid, String metricsType) {
        return null;
    }

    @Override
    public BaseResponse getTaskCenterCreators(String projectUuid, String corpId, String taskType) {
        return null;
    }

    @Override
    public BaseResponse deleteTaskCenter(String taskType, String taskUuid) {
        return null;
    }

    @Override
    public BaseResponse checkTaskCenterName(String projectId, String taskType, Integer taskId, String name) {
        return null;
    }
}
