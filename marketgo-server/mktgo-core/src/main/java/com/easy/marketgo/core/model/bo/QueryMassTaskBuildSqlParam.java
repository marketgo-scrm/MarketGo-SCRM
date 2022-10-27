package com.easy.marketgo.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
public class QueryMassTaskBuildSqlParam {
    private String projectUuid;
    private String weComMassTaskTypeEnum;
    private String keyword;
    private String sortKey;
    private String sortOrderKey;
    private List<String> creatorIds;
    private List<String> statuses;
    private Integer pageNum;
    private Integer pageSize;
    private Integer currentAccountId;
    private Date startTime;
    private Date endTime;
    private String corpId;
    private String agentId;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
