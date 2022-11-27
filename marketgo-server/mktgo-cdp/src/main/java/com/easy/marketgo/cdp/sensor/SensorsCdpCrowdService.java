package com.easy.marketgo.cdp.sensor;

import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.model.CrowdBaseRequest;
import com.easy.marketgo.cdp.service.CdpCrowdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 4:22 PM
 * Describe:
 */

@Component
@Slf4j
public class SensorsCdpCrowdService implements CdpCrowdService {
    @Override
    public CdpCrowdListMessage queryCrowdList(CrowdBaseRequest request) {
        return null;
    }
}
