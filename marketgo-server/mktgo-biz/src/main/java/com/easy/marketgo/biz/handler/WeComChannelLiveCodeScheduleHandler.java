package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.wecom.livecode.ChannelLiveCodeRefreshService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-20 15:21:26
 * @description : WeComChannelLiveCodeScheduleHandler.java
 */
@Slf4j
@Component
public class WeComChannelLiveCodeScheduleHandler {

    @Autowired
    private ChannelLiveCodeRefreshService liveCodeRefreshService;

    @XxlJob(value = "channelLiveCodeRefresh")
    public ReturnT<String> channelLiveCodeRefresh() throws Exception {

        liveCodeRefreshService.refresh();
        return ReturnT.SUCCESS;
    }
}
