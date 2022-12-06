package com.easy.marketgo.cdp.sensor;

import com.easy.marketgo.cdp.analysys.response.CrowdMessageResponse;
import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.model.CrowdBaseRequest;
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
 * @data : 11/23/22 4:22 PM
 * Describe: https://manual.sensorsdata.cn/sa/latest/api-33292398.html  基于2.4的api
 */

@Component
@Slf4j
public class SensorsCdpCrowdService implements CdpCrowdService {

    private static final String URL_QUERY_CROWD_USERS = "/uba/api/cohort/users";

    private static final String URL_QUERY_CROWD_USERS_EXPORT = "/uba/api/cohort/users/export";

    //获取分群计算状态是成功的返回分群的详细信息
    private static final String URL_QUERY_CROWD_LIST = "/v2/user_groups";

    private static final String HTTP_PARAMS_KEY_PROJECT = "project";
    private static final String HTTP_PARAMS_KEY_TOKEN = "token";

    @Override
    public CdpCrowdListMessage queryCrowdList(CrowdBaseRequest request) {
        String requestUrl = request.getApiUrl() + URL_QUERY_CROWD_LIST;

        CdpCrowdListMessage message = new CdpCrowdListMessage();
        message.setCode(ErrorCodeEnum.OK.getCode());
        message.setMessage(ErrorCodeEnum.OK.getMessage());

        Map<String, String> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(request.getAppKey())) {
            params.put(HTTP_PARAMS_KEY_PROJECT, request.getAppKey());
        }
        params.put(HTTP_PARAMS_KEY_TOKEN, request.getApiSecret());
        String response = null;
        try {
            response = OkHttpUtils.getInstance().getDataSync(requestUrl, params);
        } catch (Exception e) {
            log.error("failed to query sensors crowd list.request={}", request, e);
        }
        if (StringUtils.isBlank(response)) {
            log.info("failed to query sensors crowd list.request={}", request);
            message.setCode(ErrorCodeEnum.ERROR_CDP_RESPONSE_IS_EMPTY.getCode());
            message.setMessage(ErrorCodeEnum.ERROR_CDP_RESPONSE_IS_EMPTY.getMessage());
            return message;
        }

        List<CrowdMessageResponse> crowdList = JsonUtils.toArray(response, CrowdMessageResponse.class);
        if (CollectionUtils.isEmpty(crowdList)) {
            log.info("failed to parser sensors crowd list.request={}", request);
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
        message.setCdpType(CdpManufacturerTypeEnum.SENSORS.getValue());
        log.info("return to query sensors crowd list.request={}", request);
        return message;
    }

    @Override
    public void queryCrowdUsers() {

    }
}
