package com.easy.marketgo.core.model.taskcenter;

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
public class QueryTaskCenterMemberBuildSqlParam {
    private List<String> taskTypes;
    private String memberId;
    private String sortOrderKey;
    private List<String> statuses;
    private Integer pageNum;
    private Integer pageSize;
    private Date startTime;
    private Date endTime;
    private String corpId;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
