package com.easy.marketgo.cdp.service.impl;

import com.easy.marketgo.cdp.service.CdpCrowdService;
import com.easy.marketgo.common.enums.cdp.CdpManufacturerTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 10:32 AM
 * Describe:
 */
@Component
@Slf4j
public class CdpStrategyFactory {

    @Autowired
    private Map<String, CdpCrowdService> cdpCrowdServiceMap;

    public CdpCrowdService getCdpCrowdService(String cdpType) {
        String service = CdpManufacturerTypeEnum.fromValue(cdpType).getService();
        log.info("query open cdp service name. service={}", service);
        return cdpCrowdServiceMap.get(service);
    }
}
