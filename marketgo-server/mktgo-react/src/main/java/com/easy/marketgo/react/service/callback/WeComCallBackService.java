package com.easy.marketgo.react.service.callback;

import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.message.WeComXmlOutMessage;
import com.easy.marketgo.react.config.WeComConfiguration;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 22:23:55
 * @description : 企业微信回调统一入口
 */
@Component
@Slf4j
public class WeComCallBackService implements IBaseService {

    public WeComXmlOutMessage receiveEvent(String mingwenxml, String corpId) {

        WeComXmlMessage message = WeComXmlMessage.fromXml(mingwenxml);

        return this.route(message, corpId);

    }

    private WeComXmlOutMessage route(WeComXmlMessage message, String corpId) {

        try {
            HashMap<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("corpId", corpId);
            return WeComConfiguration.getRouter().route(message, hashMap);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }


    private WeComXmlOutMessage route(WeComXmlMessage message) {

        try {
            return WeComConfiguration.getRouter().route(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

}
