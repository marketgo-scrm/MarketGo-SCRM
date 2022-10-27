package com.easy.marketgo.web.model.request;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 6:51 PM
 * Describe:
 */
@Data
public class WeComGroupChatRequest {
    private Integer pageNum;
    private Integer pageSize;
    private String corpId;
    private String groupChatName;
}
