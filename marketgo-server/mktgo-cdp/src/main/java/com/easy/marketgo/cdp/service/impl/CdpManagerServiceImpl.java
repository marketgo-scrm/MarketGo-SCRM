package com.easy.marketgo.cdp.service.impl;

import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.model.CrowdBaseRequest;
import com.easy.marketgo.cdp.model.CrowdUsersBaseRequest;
import com.easy.marketgo.cdp.service.CdpCrowdService;
import com.easy.marketgo.cdp.service.CdpManagerService;
import com.easy.marketgo.core.entity.cdp.CdpConfigEntity;
import com.easy.marketgo.core.entity.cdp.CdpCrowdUsersSyncEntity;
import com.easy.marketgo.core.repository.cdp.CdpConfigRepository;
import com.easy.marketgo.core.repository.cdp.CdpCrowdUsersSyncRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/22/22 10:30 PM
 * Describe:
 */
@Component
@Slf4j
public class CdpManagerServiceImpl implements CdpManagerService {

    @Autowired
    private CdpStrategyFactory cdpStrategyFactory;

    @Autowired
    private CdpConfigRepository cdpConfigRepository;

    @Autowired
    private CdpCrowdUsersSyncRepository cdpCrowdUsersSyncRepository;

    @Override
    public CdpCrowdListMessage queryCrowdList(String projectId, String corpId) {
        log.info("start to query crowd list. projectId={}, corpId={}", projectId, corpId);
        List<CdpConfigEntity> entities = cdpConfigRepository.getCdpConfigByCorpId(projectId, corpId);
        if (CollectionUtils.isEmpty(entities)) {
            log.info("failed to query cdp message. projectId={}, corpId={}", projectId, corpId);
            return null;
        }
        CdpConfigEntity entity = entities.get(0);
        String cdpType = entity.getCdpType();

        CdpCrowdService cdpCrowdService = cdpStrategyFactory.getCdpCrowdService(cdpType);

        CrowdBaseRequest request = new CrowdBaseRequest();
        BeanUtils.copyProperties(entity, request);
        request.setCorpId(corpId);
        CdpCrowdListMessage cdpCrowdListMessage = cdpCrowdService.queryCrowdList(request);
        if (cdpCrowdListMessage != null) {

        }
        return cdpCrowdListMessage;
    }

    @Override
    public void queryCrowdUsers(CrowdUsersBaseRequest request) {
        log.info("start to query crowd users. request={}", request);
        if (CollectionUtils.isEmpty(request.getCrowds())) {
            log.info("failed to query crowd users because crowd list is empty. request={}", request);
            return;
        }
        List<CdpCrowdUsersSyncEntity> entities = new ArrayList<>();
        for (CrowdUsersBaseRequest.CrowdMessage message : request.getCrowds()) {
            CdpCrowdUsersSyncEntity entity = new CdpCrowdUsersSyncEntity();
            entity.setCdpType(request.getCdpType());
            entity.setCorpId(request.getCorpId());
            entity.setUserCount(Integer.valueOf(message.getUserCount()));
            entity.setCrowdCode(message.getCode());
            entity.setCrowdName(message.getName());
//            entity.setProjectName(request.getProjectName());
            entity.setTaskUuid(request.getTaskUuid());
            entities.add(entity);
        }
        cdpCrowdUsersSyncRepository.saveAll(entities);
    }

    @Override
    public void downloadCrowdUsers(CrowdUsersBaseRequest request) {

    }
}
