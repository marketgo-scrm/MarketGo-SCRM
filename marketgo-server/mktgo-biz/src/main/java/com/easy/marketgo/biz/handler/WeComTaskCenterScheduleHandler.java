package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.wecom.QueryUserGroupDetailService;
import com.easy.marketgo.biz.service.wecom.masstask.QueryMassTaskMetricsService;
import com.easy.marketgo.biz.service.wecom.masstask.SendGroupMassTaskProducer;
import com.easy.marketgo.biz.service.wecom.masstask.SendMomentMassTaskProducer;
import com.easy.marketgo.biz.service.wecom.masstask.SendSingleMassTaskProducer;
import com.easy.marketgo.biz.service.wecom.taskcenter.UserGroupDetailComputeService;
import com.easy.marketgo.common.enums.WeComMassTaskSendIdType;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/22/22 12:08 AM
 * Describe:
 */
@Slf4j
@Component
public class WeComTaskCenterScheduleHandler {
    @Autowired
    private UserGroupDetailComputeService userGroupDetailComputeService;

    @Autowired
    private SendSingleMassTaskProducer sendSingleMassTaskProducer;

    @Autowired
    private SendGroupMassTaskProducer sendGroupMassTaskProducer;

    @Autowired
    private SendMomentMassTaskProducer sendMomentMassTaskProducer;

    @XxlJob(value = "taskCenterComputeUserGroupDetail")
    public ReturnT<String> computeUserGroupDetail() throws Exception {

        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.SINGLE.name());
        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.GROUP.name());
        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.MOMENT.name());
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "sendTaskCenterForUserGroup")
    public ReturnT<String> sendMassTaskForUserGroup() throws Exception {
        sendSingleMassTaskProducer.sendSingleMassTask();
        sendGroupMassTaskProducer.sendGroupMassTask();
        sendMomentMassTaskProducer.sendMomentMassTask();
        return ReturnT.SUCCESS;
    }

}
