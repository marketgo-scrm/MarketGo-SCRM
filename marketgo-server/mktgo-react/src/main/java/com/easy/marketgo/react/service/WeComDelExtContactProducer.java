package com.easy.marketgo.react.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.easy.marketgo.common.constants.RabbitMqConstants.EXCHANGE_NAME_DEFAULT_CHANGE_EXT_CONTACT;
import static com.easy.marketgo.common.constants.RabbitMqConstants.ROUTING_KEY_WECOM_DEL_EXT_CONTACT;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-16 20:46:56
 * @description : WeComDelExtContactProducer.java
 */
@Component
@Log4j2
@AllArgsConstructor
public class WeComDelExtContactProducer {
    @Autowired
    private RabbitTemplate weComChangeExtContactAmqpTemplate;

    public void produceRabbitMqMessage(Object values) {
        weComChangeExtContactAmqpTemplate.convertAndSend(EXCHANGE_NAME_DEFAULT_CHANGE_EXT_CONTACT,
                ROUTING_KEY_WECOM_DEL_EXT_CONTACT, values);
    }
}
