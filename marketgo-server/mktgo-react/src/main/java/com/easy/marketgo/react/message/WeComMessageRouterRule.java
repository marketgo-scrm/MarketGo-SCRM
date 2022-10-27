package com.easy.marketgo.react.message;

import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.message.WeComXmlOutMessage;
import com.easy.marketgo.react.message.handler.WeErrorExceptionHandler;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 20:46:45
 * @description : WxCpTpMessageRouterRule.java
 */
@Data
public class WeComMessageRouterRule {

    private final WeComMessageRouter routerBuilder;

    private boolean async = true;

    private String fromUser;

    private String msgType;

    private String event;

    private String eventKey;

    private String eventKeyRegex;

    private String content;

    private String rContent;

    private WeComMessageMatcher matcher;

    private boolean reEnter = false;

    private Integer agentId;

    private String infoType;

    private String changeType;

    private List<WeComMessageHandler> handlers = new ArrayList<>();

    private List<WeComMessageInterceptor> interceptors = new ArrayList<>();
    private String authCode;

    /**
     * Instantiates a new Wx cp message router rule.
     *
     * @param routerBuilder the router builder
     */
    protected WeComMessageRouterRule(WeComMessageRouter routerBuilder) {

        this.routerBuilder = routerBuilder;
    }

    /**
     * 设置是否异步执行，默认是true
     *
     * @param async the async
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule async(boolean async) {

        this.async = async;
        return this;
    }

    /**
     * 如果msgType等于某值
     *
     * @param msgType the msg type
     * @return the wx cp tp message router rule
     */
    public WeComMessageRouterRule msgType(String msgType) {

        this.msgType = msgType;
        return this;
    }

    /**
     * 如果event等于某值
     *
     * @param event the event
     * @return the wx cp tp message router rule
     */
    public WeComMessageRouterRule event(String event) {

        this.event = event;
        return this;
    }

    /**
     * 匹配 Message infoType
     *
     * @param infoType info
     */
    public WeComMessageRouterRule infoType(String infoType) {

        this.infoType = infoType;
        return this;
    }

    /**
     * 如果changeType等于这个type，符合rule的条件之一
     *
     * @param changeType
     * @return
     */
    public WeComMessageRouterRule changeType(String changeType) {

        this.changeType = changeType;
        return this;
    }


    /**
     * 如果消息匹配某个matcher，用在用户需要自定义更复杂的匹配规则的时候
     *
     * @param matcher the matcher
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule matcher(WeComMessageMatcher matcher) {
        this.matcher = matcher;
        return this;
    }

    /**
     * 设置微信消息拦截器
     *
     * @param interceptor the interceptor
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule interceptor(WeComMessageInterceptor interceptor) {

        return interceptor(interceptor, (WeComMessageInterceptor[]) null);
    }

    /**
     * 设置微信消息拦截器
     *
     * @param interceptor       the interceptor
     * @param otherInterceptors the other interceptors
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule interceptor(WeComMessageInterceptor interceptor, WeComMessageInterceptor... otherInterceptors) {

        this.interceptors.add(interceptor);
        if (otherInterceptors != null && otherInterceptors.length > 0) {
            Collections.addAll(this.interceptors, otherInterceptors);
        }
        return this;
    }

    /**
     * 设置微信消息处理器
     *
     * @param handler the handler
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule handler(WeComMessageHandler handler) {

        return handler(handler, (WeComMessageHandler[]) null);
    }

    /**
     * 设置微信消息处理器
     *
     * @param handler       the handler
     * @param otherHandlers the other handlers
     * @return the wx cp message router rule
     */
    public WeComMessageRouterRule handler(WeComMessageHandler handler, WeComMessageHandler... otherHandlers) {

        this.handlers.add(handler);
        if (otherHandlers != null && otherHandlers.length > 0) {
            Collections.addAll(this.handlers, otherHandlers);
        }
        return this;
    }

    /**
     * 规则结束，但是消息还会进入其他规则
     *
     * @return the wx cp message router
     */
    public WeComMessageRouter next() {

        this.reEnter = true;
        return end();
    }

    /**
     * 规则结束，代表如果一个消息匹配该规则，那么它将不再会进入其他规则
     *
     * @return the wx cp message router
     */
    public WeComMessageRouter end() {

        this.routerBuilder.getRules().add(this);
        return this.routerBuilder;
    }

    /**
     * Test boolean.
     *
     * @param wxMessage the wx message
     * @return the boolean
     */
    protected boolean test(WeComXmlMessage wxMessage) {

        boolean b =
                (this.agentId == null || this.agentId.equals(wxMessage.getAgentId()))
                &&
                (this.msgType == null || this.msgType.equalsIgnoreCase(wxMessage.getMsgType()))
                &&
                (this.event == null || this.event.equalsIgnoreCase(wxMessage.getEvent()))
                &&
                (this.infoType == null || this.infoType.equals(wxMessage.getInfoType()))
                &&
                (this.eventKeyRegex == null || Pattern.matches(this.eventKeyRegex, StringUtils.trimToEmpty(wxMessage.getEventKey())))
                &&
                (this.content == null || this.content.equals(StringUtils.trimToNull(wxMessage.getContent())))
                &&
                (this.rContent == null || Pattern.matches(this.rContent, StringUtils.trimToEmpty(wxMessage.getContent())))
                &&
                (this.changeType == null || this.changeType.equals(wxMessage.getChangeType()))
                &&
                (this.matcher == null || this.matcher.match(wxMessage));

        return b;
    }

    /**
     * 处理微信推送过来的消息
     *
     * @param wxMessage        the wx message
     * @param context          the context
     * @param exceptionHandler the exception handler
     * @return true 代表继续执行别的router，false 代表停止执行别的router
     */
    protected WeComXmlOutMessage service(WeComXmlMessage wxMessage,
                                         Map<String, Object> context,
                                         WeErrorExceptionHandler exceptionHandler) {

        if (context == null) {
            context = new HashMap<>(2);
        }

        try {
            // 如果拦截器不通过
            for (WeComMessageInterceptor interceptor : this.interceptors) {
                if (!interceptor.intercept(wxMessage, context)) {
                    return null;
                }
            }

            // 交给handler处理
            WeComXmlOutMessage res = null;
            for (WeComMessageHandler handler : this.handlers) {
                // 返回最后handler的结果
                res = handler.handle(wxMessage, context);
            }
            return res;

        } catch (Exception e) {
            exceptionHandler.handle(e);
        }

        return null;
    }
}
