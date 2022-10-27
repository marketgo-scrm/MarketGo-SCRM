package com.easy.marketgo.core.config;

import com.easy.marketgo.common.constants.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/3/22 9:52 PM
 * Describe:
 */
@Configuration
public class WeComGroupChatCallbackMsgRabbitMqConfig extends AbstractRabbitConfig {

    @Bean
    public RabbitTemplate weComGroupChatCallbackMsgAmqpTemplate() {

        RabbitProperties rabbitProperties = this.rabbitProperties();
        rabbitProperties.setVirtualHost(RabbitMqConstants.VIRTUAL_HOST_WECOM_MASS_TASK);

        RabbitTemplate rabbitTemplate = this.rabbitTemplate(rabbitProperties);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        rabbitAdmin.declareExchange(this.weComGroupChatCallbackMsgExchange());

        rabbitAdmin.declareExchange(this.failureQueueExchange());
        rabbitAdmin.declareQueue(this.failureQueueQueue());
        rabbitAdmin.declareBinding(this.failureQueueBinging());

        rabbitAdmin.declareQueue(this.weComGroupChatCallbackMsgQueue());
        rabbitAdmin.declareBinding(this.weComGroupChatCallbackMsgBinging());

        return rabbitTemplate;

    }

    @Bean
    public TopicExchange failureQueueExchange() {
        TopicExchange topicExchange = new TopicExchange(RabbitMqConstants.EXCHANGE_NAME_FAILURE_QUEUE);
        topicExchange.setShouldDeclare(false);
        return topicExchange;
    }

    @Bean
    public Queue failureQueueQueue() {
        return new Queue(RabbitMqConstants.FUNNEL_QUEUE_NAME_FAILURE_MSG, true);
    }

    @Bean
    public Binding failureQueueBinging() {
        return BindingBuilder.bind(this.failureQueueQueue()).to(this.failureQueueExchange())
                .with(RabbitMqConstants.FUNNEL_ROUTING_FAILURE_MSG);
    }

    @Bean
    public TopicExchange  weComGroupChatCallbackMsgExchange() {

        TopicExchange topicExchange = new TopicExchange(RabbitMqConstants.EXCHANGE_NAME_DEFAULT_WECOM_GROUP_CHAT);
        topicExchange.setShouldDeclare(false);
        return topicExchange;
    }

    @Bean(name = "weComGroupChatCallbackMsgListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory weComGroupChatCallbackMsgListenerContainerFactory() {

        RabbitProperties rabbitProperties = this.rabbitProperties();
        rabbitProperties.setVirtualHost(RabbitMqConstants.VIRTUAL_HOST_WECOM_MASS_TASK);
        return this.rabbitListenerContainerFactory(rabbitProperties);
    }


    @Bean
    public Queue weComGroupChatCallbackMsgQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", RabbitMqConstants.EXCHANGE_NAME_FAILURE_QUEUE);
        params.put("x-dead-letter-routing-key", RabbitMqConstants.FUNNEL_ROUTING_FAILURE_MSG);
        return new Queue(RabbitMqConstants.QUEUE_NAME_WECOM_GROUP_CHAT, true, false, false, params);
    }

    @Bean
    public Binding weComGroupChatCallbackMsgBinging() {

        return BindingBuilder.bind(this.weComGroupChatCallbackMsgQueue()).to(this.weComGroupChatCallbackMsgExchange())
                .with(RabbitMqConstants.ROUTING_KEY_WECOM_GROUP_CHAT);
    }
}
