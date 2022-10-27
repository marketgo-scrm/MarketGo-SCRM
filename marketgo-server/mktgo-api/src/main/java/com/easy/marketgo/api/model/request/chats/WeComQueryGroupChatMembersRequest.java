package com.easy.marketgo.api.model.request.chats;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 12:05 PM
 * Describe:
 */
@Data
public class WeComQueryGroupChatMembersRequest extends BaseRpcRequest {
    private String chatId;

    private Integer needName;
}
