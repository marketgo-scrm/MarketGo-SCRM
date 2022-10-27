package com.easy.marketgo.core.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class QueryMemberBuildSqlParam {
    private String corpId;
    private List<Long> departments;
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 10000;

    public Integer getStartIndex() {
        return (pageNum == null) ? null : ((pageNum - 1) * pageSize);
    }
}
