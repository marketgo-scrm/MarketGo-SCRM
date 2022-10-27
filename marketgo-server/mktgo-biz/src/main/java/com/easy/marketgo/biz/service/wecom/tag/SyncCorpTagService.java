package com.easy.marketgo.biz.service.wecom.tag;

import com.easy.marketgo.api.model.request.tag.WeComQueryTagsClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.tag.WeComQueryCorpTagsClientResponse;
import com.easy.marketgo.api.service.WeComTagsRpcService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.utils.DateFormatUtils;
import com.easy.marketgo.core.entity.tag.WeComCorpTagEntity;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComCorpTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.easy.marketgo.common.utils.DateFormatUtils.DATETIME;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/22/22 2:19 PM
 * Describe:
 */
@Slf4j
@Service
public class SyncCorpTagService {

    @Resource
    private WeComTagsRpcService weComTagsRpcService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComCorpTagRepository weComCorpTagRepository;

    @Transactional(rollbackFor = Exception.class)
    public void queryCorpTags(final String projectUuid, final String corpId) {
        try {
            WeComQueryCorpTagsClientResponse data = queryCorpTagsForCorpId(corpId);
            if (data == null) {
                log.info("response WeComQueryCorpTagsClientResponse is empty.");
                return;
            }
            List<WeComQueryCorpTagsClientResponse.TagGroup> tagGroups = data.getTagGroup();
            if (CollectionUtils.isEmpty(tagGroups)) {
                log.info("response corp tags list is empty");
                return;
            }
            saveCorpTagsMessage(corpId, tagGroups);
        } catch (Exception e) {
            log.error("failed to save weCom sync contacts. ", e);
        }
    }

    public WeComQueryCorpTagsClientResponse queryCorpTagsForCorpId(String corpId) {
        WeComQueryTagsClientRequest request = new WeComQueryTagsClientRequest();
        request.setCorpId(corpId);
        request.setAgentId(Constants.AGENT_KEY_FOR_EXTERNALUSER);
        RpcResponse<WeComQueryCorpTagsClientResponse> rpcResponse = weComTagsRpcService.queryCorpTags(request);
        log.info("get corp tags response. response={}", rpcResponse);
        WeComQueryCorpTagsClientResponse data = rpcResponse.getData();
        return data;
    }

    private void saveCorpTagsMessage(String corpId, List<WeComQueryCorpTagsClientResponse.TagGroup> tagGroups) {
        weComCorpTagRepository.deleteByCorpId(corpId);

        tagGroups.forEach(item -> {
            List<WeComCorpTagEntity> tagEntities = new ArrayList<>();
            WeComCorpTagEntity entity = new WeComCorpTagEntity();

            entity.setAddTime(DateFormatUtils.parseDate(item.getCreateTime() * 1000, DATETIME));
            entity.setCorpId(corpId);
            entity.setTagId(item.getGroupId());
            entity.setGroupId(item.getGroupId());
            entity.setName(item.getGroupName());
            entity.setOrder(item.getOrder());
            entity.setType(Boolean.FALSE);
            entity.setDeleted(item.getDeleted() == null ? Boolean.FALSE : item.getDeleted());
            tagEntities.add(entity);
            if (CollectionUtils.isNotEmpty(item.getTag())) {
                item.getTag().forEach(tag -> {
                    WeComCorpTagEntity tagEntity = new WeComCorpTagEntity();
                    tagEntity.setAddTime(DateFormatUtils.parseDate(tag.getCreateTime() * 1000, DATETIME));
                    tagEntity.setCorpId(corpId);
                    tagEntity.setTagId(tag.getId());
                    tagEntity.setName(tag.getName());
                    tagEntity.setOrder(tag.getOrder());
                    tagEntity.setType(Boolean.TRUE);
                    tagEntity.setDeleted(tag.getDeleted() == null ? Boolean.FALSE : tag.getDeleted());
                    tagEntity.setGroupId(item.getGroupId());
                    tagEntities.add(tagEntity);

                });
            }
            weComCorpTagRepository.saveAll(tagEntities);
        });
    }
}
