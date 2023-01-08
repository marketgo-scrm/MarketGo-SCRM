package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.wecom.taskcenter.*;
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
    private SendSingleTaskCenterProducer sendSingleTaskCenterProducer;

    @Autowired
    private SendGroupTaskCenterProducer sendGroupTaskCenterProducer;

    @Autowired
    private SendMomentTaskCenterProducer sendMomentTaskCenterProducer;

    @Autowired
    private ContentUpdateCacheService contentUpdateCacheService;

    @XxlJob(value = "taskCenterComputeUserGroupDetail")
    public ReturnT<String> computeUserGroupDetail() throws Exception {

        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.SINGLE.name());
        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.GROUP.name());
        userGroupDetailComputeService.queryWeComTaskCenterUserGroup(WeComMassTaskTypeEnum.MOMENT.name());
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "sendTaskCenterForUserGroup")
    public ReturnT<String> sendMassTaskForUserGroup() throws Exception {
        sendSingleTaskCenterProducer.sendSingleTask();
        sendGroupTaskCenterProducer.sendGroupTask();
        sendMomentTaskCenterProducer.sendMomentTask();
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "updateTaskCenterContent")
    public ReturnT<String> updateTaskCenterContent() throws Exception {
        contentUpdateCacheService.checkContentExpireIn();
        return ReturnT.SUCCESS;
    }
}
