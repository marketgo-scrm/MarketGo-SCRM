package com.easy.marketgo.core.model.wecom;

import com.easy.marketgo.common.enums.SdkConfigSignatureTyeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 11:06
 * Describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryApiTicketRequest extends QueryTokenBaseRequest {
    private SdkConfigSignatureTyeEnum type;
}
