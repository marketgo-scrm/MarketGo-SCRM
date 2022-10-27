package com.easy.marketgo.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 9:48 PM
 * Describe:
 */
@UtilityClass
public class UuidUtils {
    public String generateUuid(String suffix) {
        return (StringUtils.isBlank(suffix) ? "" : suffix) + UUID.randomUUID().toString().replace("-", "");
    }

    public String generateUuid() {
        return generateUuid("");
    }
}
