package com.easy.marketgo.cdp.sensor;

import com.easy.marketgo.core.model.cdp.CdpCrowdListMessage;
import com.easy.marketgo.core.model.cdp.CrowdBaseRequest;
import com.easy.marketgo.cdp.service.CdpCrowdService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.cdp.CdpManufacturerTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        List<CdpCrowdListMessage.CrowdMessage> messageList = new ArrayList<>();

        CdpCrowdListMessage.CrowdMessage message1 = new CdpCrowdListMessage.CrowdMessage();
        message1.setCode("fsdafsdf");
        message1.setName("新访用户");
        message1.setUserCount("1000");
        messageList.add(message1);

        CdpCrowdListMessage.CrowdMessage message2 = new CdpCrowdListMessage.CrowdMessage();
        message2.setCode("fsdafsdfvip");
        message2.setName("VIP用户");
        message2.setUserCount("200000");
        messageList.add(message2);

        CdpCrowdListMessage.CrowdMessage message3 = new CdpCrowdListMessage.CrowdMessage();
        message3.setCode("fsdafsdfvvip");
        message3.setName("VVIP用户");
        message3.setUserCount("10000");
        messageList.add(message3);
       /* Map<String, String> params = Maps.newHashMap();
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
        }*/
        message.setCrowds(messageList);
        message.setCdpType(CdpManufacturerTypeEnum.SENSORS.getValue());
        log.info("return to query sensors crowd list.request={}", request);
        return message;
    }

    @Override
    public void queryCrowdUsers() {

    }
}
