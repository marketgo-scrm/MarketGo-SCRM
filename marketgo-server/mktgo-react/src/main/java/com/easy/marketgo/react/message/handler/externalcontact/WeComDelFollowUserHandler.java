package com.easy.marketgo.react.message.handler.externalcontact;

import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.message.WeComXmlOutMessage;
import com.easy.marketgo.react.message.handler.AbstractWeComHandler;
import com.easy.marketgo.react.service.WeComDelFollowUserProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-16 20:21:27
 * @description : WeComDelFollowUserHandler.java
 */
@Slf4j
@Component
public class WeComDelFollowUserHandler extends AbstractWeComHandler {

    @Autowired
    private WeComDelFollowUserProducer delExtContactProducer;

    /**
     * Handle wx cp xml out message.
     *
     * @param weComXmlMessage the wx message
     * @param context         上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @return xml格式的消息 ，如果在异步规则里处理的话，可以返回null
     * @throws CommonException the wx error exception
     */
    @Override
    public WeComXmlOutMessage handle(WeComXmlMessage weComXmlMessage, Map<String, Object> context) throws CommonException {

        log.info("{}", weComXmlMessage);
        log.info("{}", context);
        delExtContactProducer.produceRabbitMqMessage(weComXmlMessage);
        return null;
    }
}
