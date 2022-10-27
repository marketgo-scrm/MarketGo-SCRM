package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatMembersRequest;
import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatsRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComGroupChatsRpcService;
import com.easy.marketgo.gateway.wecom.request.customer.QueryGroupChatMembersRequest;
import com.easy.marketgo.gateway.wecom.request.customer.QueryGroupChatRequest;
import com.easy.marketgo.gateway.wecom.sevice.GroupChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 12:08 PM
 * Describe:
 */
@Slf4j
@DubboService
public class WeComGroupChatsRpcServiceImpl implements WeComGroupChatsRpcService {

    @Autowired
    private GroupChatService groupChatService;

    @Override
    public RpcResponse queryGroupChats(WeComQueryGroupChatsRequest request) {
        log.info("receive rpc for query group chats");
        QueryGroupChatRequest queryGroupChatRequest = new QueryGroupChatRequest();
        BeanUtils.copyProperties(request, queryGroupChatRequest);
        return groupChatService.queryGroupChats(request.getCorpId(), request.getAgentId(), queryGroupChatRequest);
    }

    @Override
    public RpcResponse queryGroupChatMembers(WeComQueryGroupChatMembersRequest request) {
        log.info("receive rpc for query group chat for member");
        QueryGroupChatMembersRequest queryGroupChatMembersRequest = new QueryGroupChatMembersRequest();
        BeanUtils.copyProperties(request, queryGroupChatMembersRequest);
        return groupChatService.queryGroupChatMembers(request.getCorpId(), request.getAgentId(),
                queryGroupChatMembersRequest);
    }
}
