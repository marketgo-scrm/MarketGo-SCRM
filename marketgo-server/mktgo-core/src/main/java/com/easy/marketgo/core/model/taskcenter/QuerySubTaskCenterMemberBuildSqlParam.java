package com.easy.marketgo.core.model.taskcenter;

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
public class QuerySubTaskCenterMemberBuildSqlParam {
    private String corpId;
    private String memberId;
    private String sortOrderKey;
    private String taskUuid;
    private Integer pageNum;
    private Integer pageSize;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
