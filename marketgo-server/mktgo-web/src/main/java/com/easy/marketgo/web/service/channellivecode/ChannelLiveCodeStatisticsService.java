package com.easy.marketgo.web.service.channellivecode;

import com.easy.marketgo.core.model.bo.BaseResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-31 17:02:46
 * @description : ChannelLiveCodeStatisticsService.java
 */
public interface ChannelLiveCodeStatisticsService {
    BaseResponse summary(String projectId, String corpId, String channelId);

    BaseResponse statisticsList(String projectId, String corpId, String channelId,
                                                     Integer pageNum, Integer pageSize, String StatisticsType,
                                                     String startTime, String endTime);
}
