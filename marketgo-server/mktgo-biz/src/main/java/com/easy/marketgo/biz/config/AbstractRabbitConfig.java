package com.easy.marketgo.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.Resource;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/3/22 9:52 PM
 * Describe:
 */

@Slf4j
@Configuration
public abstract class AbstractRabbitConfig {
    @Resource
    private ObjectMapper objectMapper;
    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate(RabbitProperties rabbitProperties) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory(rabbitProperties));
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }

    @Bean
    @Scope("prototype")
    public ConnectionFactory connectionFactory(RabbitProperties rabbitProperties) {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(),
                rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setConnectionTimeout(50000);
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        return connectionFactory;
    }

    @Bean
    @Scope("prototype")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(RabbitProperties rabbitProperties) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory(rabbitProperties));
        RabbitProperties.SimpleContainer container = rabbitProperties.getListener()
                .getSimple();
        factory.setConcurrentConsumers(container.getConcurrency());
        factory.setMaxConcurrentConsumers(container.getMaxConcurrency());
        factory.setChannelTransacted(true);
//        factory.setAcknowledgeMode(rabbitProperties.getListener().getSimple().getAcknowledgeMode());
        factory.setPrefetchCount(1);
        factory.setAdviceChain(
                RetryInterceptorBuilder
                        .stateless()
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .retryOperations(rabbitRetryTemplate(rabbitProperties))
                        .build()
        );
        return factory;
    }

    @Bean
    @Scope("prototype")
    public RetryTemplate rabbitRetryTemplate(RabbitProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 设置监听（不是必须）
        retryTemplate.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
                // 执行之前调用 （返回false时会终止执行）
                return true;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback,
                                                       Throwable throwable) {
                // 重试结束的时候调用 （最后一次重试 ）
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext retryContext, RetryCallback<T, E> retryCallback
                    , Throwable throwable) {
                //  异常 都会调用
                log.error("-----第{}次调用", retryContext.getRetryCount());
            }
        });

        // 个性化处理异常和重试 （不是必须）
        /* Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        //设置重试异常和是否重试
        retryableExceptions.put(AmqpException.class, true);
        //设置重试次数和要重试的异常
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(5,retryableExceptions);*/

        retryTemplate.setBackOffPolicy(backOffPolicyByProperties(properties));
        retryTemplate.setRetryPolicy(retryPolicyByProperties(properties));
        return retryTemplate;
    }

    @Bean
    @Scope("prototype")
    public ExponentialBackOffPolicy backOffPolicyByProperties(RabbitProperties properties) {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        long maxInterval = properties.getListener().getSimple().getRetry().getMaxInterval().getSeconds();
        long initialInterval = properties.getListener().getSimple().getRetry().getInitialInterval().getSeconds();
        double multiplier = properties.getListener().getSimple().getRetry().getMultiplier();
        // 重试间隔
        backOffPolicy.setInitialInterval(initialInterval * 1000);
        // 重试最大间隔
        backOffPolicy.setMaxInterval(maxInterval * 1000);
        // 重试间隔乘法策略
        backOffPolicy.setMultiplier(multiplier);

        log.error("rabbitConfig:initialInterval={}, maxInterval={}", initialInterval, maxInterval);
        return backOffPolicy;
    }

    @Bean
    @Scope("prototype")
    public SimpleRetryPolicy retryPolicyByProperties(RabbitProperties properties) {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        int maxAttempts = properties.getListener().getSimple().getRetry().getMaxAttempts();
        retryPolicy.setMaxAttempts(maxAttempts);
        return retryPolicy;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public RabbitProperties rabbitProperties() {
        RabbitProperties rabbitProperties = new RabbitProperties();
        return rabbitProperties;
    }
}