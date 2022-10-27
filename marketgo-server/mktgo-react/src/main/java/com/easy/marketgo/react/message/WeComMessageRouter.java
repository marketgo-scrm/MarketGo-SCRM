package com.easy.marketgo.react.message;

import com.easy.marketgo.common.message.WeComXmlOutMessage;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.react.message.handler.WeErrorExceptionHandler;
import com.easy.marketgo.react.message.handler.log.LogExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 微信消息路由器，通过代码化的配置，把来自微信的消息交给handler处理
 * 和WxCpMessageRouter的rule相比，多了infoType和changeType维度的匹配
 *
 * 说明：
 * 1. 配置路由规则时要按照从细到粗的原则，否则可能消息可能会被提前处理
 * 2. 默认情况下消息只会被处理一次，除非使用 {@link WeComMessageRouterRule#next()}
 * 3. 规则的结束必须用{@link WeComMessageRouterRule#end()}或者{@link WeComMessageRouterRule#next()}，否则不会生效
 *
 * 使用方法：
 * WxCpTpMessageRouter router = new WxCpTpMessageRouter();
 * router
 *   .rule()
 *       .msgType("MSG_TYPE").event("EVENT").eventKey("EVENT_KEY").content("CONTENT")
 *       .interceptor(interceptor, ...).handler(handler, ...)
 *   .end()
 *   .rule()
 *       .infoType("INFO_TYPE").changeType("CHANGE_TYPE")
 *       // 另外一个匹配规则
 *   .end()
 * ;
 *
 * // 将WxXmlMessage交给消息路由器
 * router.route(message);
 *
 * </pre>
 *
 * @author ssk
 */
@Slf4j
public class WeComMessageRouter {
    private static final int DEFAULT_THREAD_POOL_SIZE = 100;
    private final List<WeComMessageRouterRule> rules = new ArrayList<>();

    private ExecutorService executorService;

    private WeErrorExceptionHandler exceptionHandler;

    /**
     * 构造方法.
     */
    public WeComMessageRouter() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("WxCpTpMessageRouter-pool-%d").build();
        this.executorService = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);
        this.exceptionHandler = new LogExceptionHandler();
    }

    /**
     * <pre>
     * 设置自定义的 {@link ExecutorService}
     * 如果不调用该方法，默认使用 Executors.newFixedThreadPool(100)
     * </pre>
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }


    /**
     * <pre>
     * 设置自定义的{@link WeErrorExceptionHandler}
     * 如果不调用该方法，默认使用 {@link LogExceptionHandler}
     * </pre>
     */
    public void setExceptionHandler(WeErrorExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    List<WeComMessageRouterRule> getRules() {
        return this.rules;
    }

    /**
     * 开始一个新的Route规则.
     */
    public WeComMessageRouterRule rule() {
        return new WeComMessageRouterRule(this);
    }

    /**
     * 处理微信消息.
     *
     */
    public WeComXmlOutMessage route(WeComXmlMessage weComXmlMessage, Map<String, Object> context) {

        List<WeComMessageRouterRule> matchRules = new ArrayList<>();
        // 收集匹配的规则
        for (WeComMessageRouterRule rule : this.rules) {
            if (rule.test(weComXmlMessage)) {
                matchRules.add(rule);
                if (!rule.isReEnter()) {
                    break;
                }
            }
        }

        if (matchRules.size() == 0) {
            return null;
        }

        WeComXmlOutMessage res = null;
        List<Future> futures = new ArrayList<>();

        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        for (WeComMessageRouterRule rule : matchRules) {
            // 返回最后一个非异步的rule的执行结果
            if (rule.isAsync()) {
                futures.add(
                        this.executorService.submit(() -> {
                            MDC.setContextMap(contextMap);
                            rule.service(weComXmlMessage, context, WeComMessageRouter.this.exceptionHandler);
                            MDC.clear();
                        })
                );
            } else {
                res = rule.service(weComXmlMessage, context,  this.exceptionHandler);

            }
        }

        if (futures.size() > 0) {
            this.executorService.submit(() -> {
                for (Future future : futures) {
                    try {
                        future.get();

                    } catch (InterruptedException e) {
                        log.error("Error happened when wait task finish", e);
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        log.error("Error happened when wait task finish", e);
                    }
                }
            });
        }
        return res;
    }

    /**
     * 处理微信消息.
     */
    public WeComXmlOutMessage route(WeComXmlMessage weComXmlMessage) {
        return this.route(weComXmlMessage, new HashMap<>(2));
    }
}
