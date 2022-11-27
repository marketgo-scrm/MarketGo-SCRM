package com.easy.marketgo.cdp.service;

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

    private static final String QUERY_STRING_SUFFIX = "CdpCrowdService";

    @Autowired
    private Map<String, CdpCrowdService> CdpCrowdServiceMap;

    public CdpCrowdService getCdpCrowdService(String cdpType) {
        return CdpCrowdServiceMap.get(cdpType + CdpCrowdServiceMap);
    }
}
