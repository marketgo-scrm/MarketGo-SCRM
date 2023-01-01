package com.easy.marketgo.cdp.service.impl;

import com.easy.marketgo.cdp.service.CdpCrowdService;
import com.easy.marketgo.core.model.cdp.*;
import com.easy.marketgo.core.service.cdp.CdpManagerService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
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
    public CdpTestSettingResponse testCdpSetting(String corpId, String cdpType, CdpTestSettingRequest request) {
        CdpTestSettingResponse response = new CdpTestSettingResponse();

        CdpCrowdService cdpCrowdService = cdpStrategyFactory.getCdpCrowdService(cdpType);

        CrowdBaseRequest crowdBaseRequest = new CrowdBaseRequest();
        BeanUtils.copyProperties(request, crowdBaseRequest);
        crowdBaseRequest.setCorpId(corpId);
        CdpCrowdListMessage cdpCrowdListMessage = cdpCrowdService.queryCrowdList(crowdBaseRequest);
        if (cdpCrowdListMessage.getCode().equals(ErrorCodeEnum.OK.getCode())) {
            response.setCode(cdpCrowdListMessage.getCode());
            response.setMessage(cdpCrowdListMessage.getMessage());
        }

        return response;
    }

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
        log.info("query open cdp config. entity={}", entity);
        CdpCrowdService cdpCrowdService = cdpStrategyFactory.getCdpCrowdService(cdpType);
        log.info("get open cdp service. cdpCrowdService={}", cdpCrowdService);
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
