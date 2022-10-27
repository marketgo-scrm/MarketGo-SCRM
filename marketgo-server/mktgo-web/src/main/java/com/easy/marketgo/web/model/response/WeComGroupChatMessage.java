package com.easy.marketgo.web.model.response;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 6:06 PM
 * Describe:
 */
@Data
public class WeComGroupChatMessage {
    /**
     * 企业微信群ID
     */
    private String chatId;
    /**
     * 企业微信群名称
     */
    private String name;
}
