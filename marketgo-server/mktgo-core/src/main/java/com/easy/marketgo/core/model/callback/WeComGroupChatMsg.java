package com.easy.marketgo.core.model.callback;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/25/22 6:05 PM
 * Describe:
 */
@Data
public class WeComGroupChatMsg {
    private String changeType;
    private String corpId;
    private String chatId;
    private String updateDetail;
    private Integer joinScene;
    private Integer quitScene;
    private Integer memberChangeCount;
    private Long createTime;
}
