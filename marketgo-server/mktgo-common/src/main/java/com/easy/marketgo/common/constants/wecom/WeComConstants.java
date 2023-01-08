package com.easy.marketgo.common.constants.wecom;

import lombok.experimental.UtilityClass;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 11:33
 * Describe:
 */
@UtilityClass
public class WeComConstants {
    public final String CACHE_KEY_AGENT_ACCESS_TOKEN = "wecom_agent_token_%s_%s";
    public final String CACHE_KEY_CORP_API_TICKET = "wecom_corp_api_ticket_%s";
    public final String CACHE_KEY_AGENT_API_TICKET = "wecom_agent_api_ticket_%s_%s";
}
