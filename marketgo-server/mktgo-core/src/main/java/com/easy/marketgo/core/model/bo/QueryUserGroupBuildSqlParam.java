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
public class QueryUserGroupBuildSqlParam {
    private Integer pageNum;
    private Integer pageSize;
    private String corpId;
    private String relation;
    private boolean duplicate;
    private List<String> memberIds;
    private String tagRelation;
    private List<String> tags;
    private List<Integer> genders;
    private List<String> groupChats;
    private String startTime;
    private String endTime;

    private List<Integer> excludeGenders;
    private String excludeRelation;
    private List<String> excludeMemberIds;
    private String excludeTagRelation;
    private List<String> excludeTags;
    private List<String> excludeGroupChats;
    private String excludeStartTime;
    private String excludeEndTime;
    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
