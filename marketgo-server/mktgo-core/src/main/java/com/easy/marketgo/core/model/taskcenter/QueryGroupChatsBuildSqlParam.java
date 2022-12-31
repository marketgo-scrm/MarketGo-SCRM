package com.easy.marketgo.core.model.taskcenter;

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
public class QueryGroupChatsBuildSqlParam {
    private Integer pageNum;
    private Integer pageSize;
    private String corpId;
    private String groupChatName;
    private String relation;
    private List<String> memberIds;
    private List<String> groupChatIds;
    private String userCountFunction;
    private Integer userCount;
    private String startTime;
    private String endTime;

    private String excludeRelation;
    private String excludeGroupChatName;
    private List<String> excludeGroupChatIds;
    private String excludeUserCountFunction;
    private Integer excludeUserCount;
    private String excludeStartTime;
    private String excludeEndTime;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
