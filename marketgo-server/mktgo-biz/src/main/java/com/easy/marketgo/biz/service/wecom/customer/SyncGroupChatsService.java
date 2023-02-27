package com.easy.marketgo.biz.service.wecom.customer;

import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatMembersRequest;
import com.easy.marketgo.api.model.request.chats.WeComQueryGroupChatsRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.chats.WeComQueryGroupChatClientResponse;
import com.easy.marketgo.api.model.response.chats.WeComQueryGroupChatMembersClientResponse;
import com.easy.marketgo.api.service.WeComGroupChatsRpcService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.DateFormatUtils;
import com.easy.marketgo.core.entity.customer.WeComGroupChatMembersEntity;
import com.easy.marketgo.core.entity.customer.WeComGroupChatsEntity;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatMembersRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/17/22 12:22 PM
 * Describe:
 */
@Slf4j
@Service
public class SyncGroupChatsService {

    @Resource
    private WeComGroupChatsRpcService weComGroupChatsRpcService;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Autowired
    private WeComGroupChatMembersRepository weComGroupChatMembersRepository;

    private ExecutorService executorService;

    private static final Integer CAPACITY_ONE = 100;
    private static final Integer CORE_THREAD_NUM = 10;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("sync_wecom_group_chat_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void queryGroupChat(final String corpId) {
        log.info("start to schedule task sync group chats message. corpId={}", corpId);

        String cursor = "";
        do {
            WeComQueryGroupChatsRequest request = new WeComQueryGroupChatsRequest();
            request.setCorpId(corpId);
            request.setAgentId(Constants.AGENT_KEY_FOR_EXTERNALUSER);
            request.setStatusFilter(0);
            request.setMemberIds(null);
            if (StringUtils.isNotBlank(cursor)) {
                request.setCursor(cursor);
            }
            request.setLimit(1000);
            log.info("schedule task sync query group chats message request. request={}", request);
            RpcResponse<WeComQueryGroupChatClientResponse> rpcResponse =
                    weComGroupChatsRpcService.queryGroupChats(request);
            log.info("schedule task sync group chats response. response={}", rpcResponse);

            if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData()
                    == null) {
                log.info("schedule task sync response WeComQueryGroupChatClientResponse is empty.");
                return;
            }
            List<WeComQueryGroupChatClientResponse.GroupChatListMessage> groupChatList =
                    rpcResponse.getData().getGroupChatList();
            if (CollectionUtils.isEmpty(groupChatList)) {
                log.info("schedule task sync response group chats list is empty");
                return;
            }
            cursor = rpcResponse.getData().getNextCursor();
            for (WeComQueryGroupChatClientResponse.GroupChatListMessage item : groupChatList) {
                syncGroupChat(corpId, item.getChatId());
            }
        } while (StringUtils.isNotBlank(cursor));
    }

    public void syncGroupChat(String corpId, String chatId) {
        Runnable task =
                new WeComQueryGroupChatMembers(corpId, Constants.AGENT_KEY_FOR_EXTERNALUSER, chatId);
        try {
            executorService.submit(task);
        } catch (Exception e) {
            log.error("failed to acquire weCom group chats from pool. ChatId={}", chatId, e);
        }
    }

    public class WeComQueryGroupChatMembers implements Runnable {
        private String chatId;
        private String corpId;
        private String agentId;

        public WeComQueryGroupChatMembers(String corpId, String agentId, String chatId) {
            this.chatId = chatId;
            this.corpId = corpId;
            this.agentId = agentId;
        }

        @Override
        public void run() {
            getGroupChatMembers(corpId, agentId, chatId);
        }

        private void getGroupChatMembers(final String corpId, final String agentId, final String chatId) {
            try {
                WeComQueryGroupChatMembersRequest request = new WeComQueryGroupChatMembersRequest();
                request.setChatId(chatId);
                request.setAgentId(agentId);
                request.setCorpId(corpId);
                request.setNeedName(1);
                log.info("schedule task sync group chats member list request. request={}", request);
                RpcResponse<WeComQueryGroupChatMembersClientResponse> rpcResponse =
                        weComGroupChatsRpcService.queryGroupChatMembers(request);
                if (rpcResponse == null || !rpcResponse.getCode().equals(ErrorCodeEnum.OK.getCode()) || rpcResponse.getData() == null) {
                    log.info("schedule task sync response WeComQueryGroupChatMembersClientResponse is empty.");
                    return;
                }
                log.info("schedule task sync get group chats member response. response={}", rpcResponse);
                WeComQueryGroupChatMembersClientResponse data = rpcResponse.getData();
                if (data.getGroupChat() == null) {
                    log.info("schedule task sync response group chats member list is empty.");
                    return;
                }
                WeComGroupChatsEntity entity =
                        weComGroupChatsRepository.queryByChatId(corpId, chatId);
                if (entity == null) {
                    entity = new WeComGroupChatsEntity();
                }
                entity.setCorpId(corpId);
                entity.setGroupChatId(chatId);
                entity.setGroupChatName(data.getGroupChat().getName());
                entity.setChatCreateTime(DateFormatUtils.parseDate(data.getGroupChat().getCreateTime() * 1000L,
                        DateFormatUtils.DATETIME));
                entity.setOwner(data.getGroupChat().getOwner());
                entity.setUserCount(data.getGroupChat().getMemberList().size());
                if (CollectionUtils.isNotEmpty(data.getGroupChat().getAdminList())) {
                    entity.setAdminList(data.getGroupChat().getAdminList().stream().map(item -> {
                        return item.getUserId();
                    }).collect(Collectors.joining(",")));
                }
                log.info("schedule task sync save group chats message. entity={}", entity);
                weComGroupChatsRepository.save(entity);
                weComGroupChatMembersRepository.deleteByCorpIdAndChatId(corpId, chatId);
                List<WeComGroupChatMembersEntity> entities = new ArrayList<>();
                for (WeComQueryGroupChatMembersClientResponse.MemberListMessage member :
                        data.getGroupChat().getMemberList()) {
                    WeComGroupChatMembersEntity membersEntity = new WeComGroupChatMembersEntity();
                    membersEntity.setCorpId(corpId);
                    membersEntity.setGroupChatId(chatId);
                    membersEntity.setGroupNickname(member.getGroupNickname());
                    membersEntity.setJoinScene(member.getJoinScene());
                    membersEntity.setUserId(member.getUserId());
                    membersEntity.setJoinTime(DateFormatUtils.parseDate(member.getJoinTime() * 1000L,
                            DateFormatUtils.DATETIME));
                    membersEntity.setUnionId(member.getUnionId());
                    membersEntity.setType(member.getType());
                    membersEntity.setName(member.getName());
                    entities.add(membersEntity);

                }
                log.info("schedule task sync save group chats message message. entities={}", entities);
                weComGroupChatMembersRepository.saveAll(entities);
            } catch (Exception e) {
                log.error("failed to schedule task sync save weCom sync group chats. ", e);
            }
        }
    }
}
