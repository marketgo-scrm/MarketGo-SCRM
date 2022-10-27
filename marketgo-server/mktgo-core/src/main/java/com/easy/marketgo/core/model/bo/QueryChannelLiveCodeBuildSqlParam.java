package com.easy.marketgo.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/21/22 6:40 PM
 * Describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryChannelLiveCodeBuildSqlParam {
    private String projectUuid;
    private String keyword;
    private String sortKey;
    private String sortOrderKey;
    private Integer pageNum;
    private Integer pageSize;
    private String corpId;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
