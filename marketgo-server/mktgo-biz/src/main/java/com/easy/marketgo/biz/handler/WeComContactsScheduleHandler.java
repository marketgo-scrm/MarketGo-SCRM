package com.easy.marketgo.biz.handler;

import com.easy.marketgo.biz.service.wecom.customer.SyncContactsService;
import com.easy.marketgo.biz.service.wecom.tag.SyncCorpTagService;
import com.easy.marketgo.biz.service.wecom.customer.SyncGroupChatsService;
import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import com.easy.marketgo.core.repository.wecom.WeComCorpMessageRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/1/22 12:07 AM
 * Describe:
 */
@Slf4j
@Component
public class WeComContactsScheduleHandler {

    @Autowired
    private WeComCorpMessageRepository weComCorpMessageRepository;

    @Autowired
    private SyncContactsService syncContactsService;

    @Autowired
    private SyncGroupChatsService syncGroupChatsService;

    @Autowired
    private SyncCorpTagService syncCorpTagService;

    @XxlJob(value = "syncMembers")
    public ReturnT<String> syncMembers() throws Exception {

        List<WeComCorpMessageEntity> entities = weComCorpMessageRepository.getCorpConfigList();
        if (CollectionUtils.isEmpty(entities)) {
            log.info("schedule task sync members to query corp config list is empty.");
            return ReturnT.SUCCESS;
        }

        for (WeComCorpMessageEntity item : entities) {
            syncContactsService.getDepartmentList(item.getProjectUuid(), item.getCorpId());
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "syncGroupChats")
    public ReturnT<String> syncGroupChats() throws Exception {
        List<WeComCorpMessageEntity> entities = weComCorpMessageRepository.getCorpConfigList();
        if (CollectionUtils.isEmpty(entities)) {
            log.info("schedule task sync group chats to query corp config list is empty.");
            return ReturnT.SUCCESS;
        }

        for (WeComCorpMessageEntity item : entities) {
            syncGroupChatsService.queryGroupChat(item.getCorpId());
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = "syncCorpTags")
    public ReturnT<String> syncCorpTags() throws Exception {
        List<WeComCorpMessageEntity> entities = weComCorpMessageRepository.getCorpConfigList();
        if (CollectionUtils.isEmpty(entities)) {
            log.info("schedule task sync corp tags to query corp config list is empty.");
            return ReturnT.SUCCESS;
        }

        for (WeComCorpMessageEntity item : entities) {
            syncCorpTagService.queryCorpTags(item.getProjectUuid(), item.getCorpId());
        }
        return ReturnT.SUCCESS;
    }
}
