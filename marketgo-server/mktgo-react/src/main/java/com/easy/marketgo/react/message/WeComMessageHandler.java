package com.easy.marketgo.react.message;

import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.message.WeComXmlOutMessage;

import java.util.Map;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 20:47:34
 * @description : 处理微信推送消息的处理器接口
 */
public interface WeComMessageHandler {

    /**
     * Handle wx cp xml out message.
     *
     * @param weComXmlMessage the wx message
     * @param context   上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @return xml格式的消息 ，如果在异步规则里处理的话，可以返回null
     * @throws CommonException the wx error exception
     */
    WeComXmlOutMessage handle(WeComXmlMessage weComXmlMessage,
                              Map<String, Object> context) throws CommonException;

}
