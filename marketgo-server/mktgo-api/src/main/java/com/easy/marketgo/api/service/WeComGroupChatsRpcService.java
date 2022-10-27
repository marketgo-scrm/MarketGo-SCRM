package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatMembersRequest;
import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatsRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/29/22 9:26 PM
 * Describe:
 */
public interface WeComGroupChatsRpcService {
    RpcResponse queryGroupChats(WeComQueryGroupChatsRequest request);

    RpcResponse queryGroupChatMembers(WeComQueryGroupChatMembersRequest request);
}
