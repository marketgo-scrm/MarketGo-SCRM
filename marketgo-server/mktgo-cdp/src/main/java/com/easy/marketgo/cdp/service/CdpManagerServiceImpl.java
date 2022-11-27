package com.easy.marketgo.cdp.service;

import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.model.CrowdBaseRequest;
import com.easy.marketgo.core.entity.CdpConfigEntity;
import com.easy.marketgo.core.repository.CdpConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    @Override
    public CdpCrowdListMessage queryCrowdList(String projectId, String corpId) {

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
    public void queryUsersForCrowd() {

    }
}
