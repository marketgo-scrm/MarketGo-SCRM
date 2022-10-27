package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.wecom.*;
import com.easy.marketgo.biz.service.wecom.masstask.QueryMassTaskMetricsService;
import com.easy.marketgo.biz.service.wecom.masstask.SendGroupMassTaskProducer;
import com.easy.marketgo.biz.service.wecom.masstask.SendMomentMassTaskProducer;
import com.easy.marketgo.biz.service.wecom.masstask.SendSingleMassTaskProducer;
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
 * @data : 7/1/22 12:08 AM
 * Describe:
 */
@Slf4j
@Component
public class WeComMassTaskScheduleHandler {
    @Autowired
    private QueryMassTaskMetricsService queryMassTaskMetricsService;

    @Autowired
    private QueryUserGroupDetailService queryUserGroupDetailService;

    @Autowired
    private SendSingleMassTaskProducer sendSingleMassTaskProducer;

    @Autowired
    private SendGroupMassTaskProducer sendGroupMassTaskProducer;

    @Autowired
    private SendMomentMassTaskProducer sendMomentMassTaskProducer;

    @XxlJob(value = "queryMassTaskMetrics")
    public ReturnT<String> queryMassTaskMetrics() throws Exception {
        queryMassTaskMetricsService.checkMassTaskSyncStatus();
        queryMassTaskMetricsService.checkMassTaskStatus();
        queryMassTaskMetricsService.startQueryMassTaskMetrics(WeComMassTaskTypeEnum.SINGLE.name(),
                WeComMassTaskSendIdType.MSG_ID.name());
        queryMassTaskMetricsService.startQueryMassTaskMetrics(WeComMassTaskTypeEnum.GROUP.name(),
                WeComMassTaskSendIdType.MSG_ID.name());
        queryMassTaskMetricsService.startQueryMassTaskMetrics(WeComMassTaskTypeEnum.MOMENT.name(),
                WeComMassTaskSendIdType.JOB_ID.name());
        queryMassTaskMetricsService.startQueryMassTaskMetrics(WeComMassTaskTypeEnum.MOMENT.name(),
                WeComMassTaskSendIdType.MOMENT_ID.name());
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "computeUserGroupDetail")
    public ReturnT<String> computeUserGroupDetail() throws Exception {

        queryUserGroupDetailService.queryWeComMassTaskUserGroup(WeComMassTaskTypeEnum.SINGLE.name());
        queryUserGroupDetailService.queryWeComMassTaskUserGroup(WeComMassTaskTypeEnum.GROUP.name());
        queryUserGroupDetailService.queryWeComMassTaskUserGroup(WeComMassTaskTypeEnum.MOMENT.name());
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "sendMassTaskForUserGroup")
    public ReturnT<String> sendMassTaskForUserGroup() throws Exception {
        sendSingleMassTaskProducer.sendSingleMassTask();
        sendGroupMassTaskProducer.sendGroupMassTask();
        sendMomentMassTaskProducer.sendMomentMassTask();
        return ReturnT.SUCCESS;
    }

}
