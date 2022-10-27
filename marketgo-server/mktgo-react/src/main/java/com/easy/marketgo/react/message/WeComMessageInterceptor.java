package com.easy.marketgo.react.message;

import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.message.WeComXmlMessage;

import java.util.Map;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 20:47:23
 * @description : 微信消息拦截器，可以用来做验证
 */
public interface WeComMessageInterceptor {

    /**
     * 拦截微信消息
     *
     * @param wxMessage the wx message
     * @param context   上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @return true代表OK ，false代表不OK
     * @throws CommonException the wx error exception
     */
    boolean intercept(WeComXmlMessage wxMessage,
                      Map<String, Object> context) throws CommonException;

}
