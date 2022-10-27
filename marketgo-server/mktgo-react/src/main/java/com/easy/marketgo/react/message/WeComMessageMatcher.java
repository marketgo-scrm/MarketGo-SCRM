package com.easy.marketgo.react.message;


import com.easy.marketgo.common.message.WeComXmlMessage;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 20:47:07
 * @description : 消息匹配器，用在消息路由的时候
 */
public interface WeComMessageMatcher {

  /**
   * 消息是否匹配某种模式
   *
   * @param message the message
   * @return the boolean
   */
  boolean match(WeComXmlMessage message);
}
