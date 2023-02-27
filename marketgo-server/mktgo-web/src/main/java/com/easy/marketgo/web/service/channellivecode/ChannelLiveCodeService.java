package com.easy.marketgo.web.service.channellivecode;

import com.easy.marketgo.web.model.request.contactway.ChannelLiveCodeCreateRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.contactway.ChannelLiveCodeResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:57:10
 * @description : ChannelContactWayService.java
 */
public interface ChannelLiveCodeService {


    ChannelLiveCodeResponse queryListByPage(String projectUuid, String corpId, Integer pageNum,
                                            Integer pageSize, String keyword);

    /**
     * @param request
     * @return
     */
    BaseResponse create(String projectUuid, String corpId, ChannelLiveCodeCreateRequest request);

    BaseResponse checkChannelName(String projectId, String corpId, Integer channelId, String name);

    BaseResponse downloadQrCode(String projectId, String corpId, String channelId, HttpServletResponse httpServletResponse);

    BaseResponse getLiveCodeDetail(String projectId, String corpId, String channelId);

    BaseResponse deleteLiveCode(String projectId, String corpId, String channelId);
}
