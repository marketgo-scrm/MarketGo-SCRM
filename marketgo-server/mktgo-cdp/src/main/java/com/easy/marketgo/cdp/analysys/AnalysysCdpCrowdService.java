package com.easy.marketgo.cdp.analysys;

import com.easy.marketgo.cdp.analysys.response.CrowdMessageResponse;
import com.easy.marketgo.core.model.cdp.CdpCrowdListMessage;
import com.easy.marketgo.core.model.cdp.CrowdBaseRequest;
import com.easy.marketgo.cdp.service.CdpCrowdService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.cdp.CdpManufacturerTypeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 12:12 PM
 * Describe: 基于API的4.5.0的版本 https://docs.analysysdata.com/uba-docs/2494992
 */
@Component
@Slf4j
public class AnalysysCdpCrowdService implements CdpCrowdService {

    private static final String URL_QUERY_CROWD_USERS = "/uba/api/cohort/users";

    private static final String URL_QUERY_CROWD_USERS_EXPORT = "/uba/api/cohort/users/export";

    //获取分群计算状态是成功的返回分群的详细信息
    private static final String URL_QUERY_CROWD_LIST = "/uba/api/cohort?needMore=true&status=success";

    private static final String HTTP_HEADER_KEY_APPKEY = "appkey";
    private static final String HTTP_HEADER_KEY_TOKEN = "token";

    @Override
    public CdpCrowdListMessage queryCrowdList(CrowdBaseRequest request) {

        CdpCrowdListMessage message = new CdpCrowdListMessage();
        message.setCode(ErrorCodeEnum.OK.getCode());
        message.setMessage(ErrorCodeEnum.OK.getMessage());
        String requestUrl = request.getApiUrl() + URL_QUERY_CROWD_LIST;

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HTTP_HEADER_KEY_APPKEY, request.getAppKey());
        headers.put(HTTP_HEADER_KEY_TOKEN, request.getApiSecret());
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getData(requestUrl, null, headers);
        } catch (Exception e) {
            log.error("failed to query analySys crowd list.request={}", request, e);
        }
        if (StringUtils.isBlank(response)) {
            log.info("failed to query analySys crowd list.request={}", request);
            message.setCode(ErrorCodeEnum.ERROR_CDP_RESPONSE_IS_EMPTY.getCode());
            message.setMessage(ErrorCodeEnum.ERROR_CDP_RESPONSE_IS_EMPTY.getMessage());
            return message;
        }

        List<CrowdMessageResponse> crowdList = JsonUtils.toArray(response, CrowdMessageResponse.class);
        if (CollectionUtils.isEmpty(crowdList)) {
            log.info("failed to parser analySys crowd list.request={}", request);
            message.setCode(ErrorCodeEnum.ERROR_CDP_CROWD_LIST_IS_EMPTY.getCode());
            message.setMessage(ErrorCodeEnum.ERROR_CDP_CROWD_LIST_IS_EMPTY.getMessage());
            return message;
        }

        List<CdpCrowdListMessage.CrowdMessage> messageList = new ArrayList<>();

        for (CrowdMessageResponse msg : crowdList) {
            CdpCrowdListMessage.CrowdMessage crowdMessage = new CdpCrowdListMessage.CrowdMessage();

            BeanUtils.copyProperties(msg, crowdMessage);
            messageList.add(crowdMessage);
        }
        message.setCrowds(messageList);
        message.setCdpType(CdpManufacturerTypeEnum.ANALYSYS.getValue());
        log.info("return to query analySys crowd list.request={}", request);
        return message;
    }

    @Override
    public void queryCrowdUsers() {

    }

}
