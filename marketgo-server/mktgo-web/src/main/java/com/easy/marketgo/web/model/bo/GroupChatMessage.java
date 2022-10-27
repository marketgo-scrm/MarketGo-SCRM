package com.easy.marketgo.web.model.bo;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 1:14 PM
 * Describe:
 */
@Data
public class GroupChatMessage {
    private String chatId;
    private String cname;
    private boolean deleted;
}
