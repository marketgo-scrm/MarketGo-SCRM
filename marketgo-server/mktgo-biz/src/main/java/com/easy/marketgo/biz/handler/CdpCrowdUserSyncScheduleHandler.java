package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.cdp.CdpCrowdUserSyncService;
import com.easy.marketgo.biz.service.wecom.MediaUploadService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/5/22 10:18 PM
 * Describe:
 */

@Slf4j
@Component
public class CdpCrowdUserSyncScheduleHandler {

    @Autowired
    private CdpCrowdUserSyncService cdpCrowdUserSyncService;

    @XxlJob(value = "CdpCrowdUserSync")
    public ReturnT<String> startCdpCrowdUserSync() throws Exception {
        cdpCrowdUserSyncService.syncCrowdUsers();
        cdpCrowdUserSyncService.computeCrowdUsers();
        return ReturnT.SUCCESS;
    }
}
