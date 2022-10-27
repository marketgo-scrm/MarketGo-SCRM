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
public class QueryExternalUserBuildSqlParam {
    private Integer pageNum;
    private Integer pageSize;
    private String corpId;
    private String externalUserName;
    private List<Integer> statuses;
    private boolean duplicate;
    private List<String> memberIds;
    private List<String> tags;
    private List<Integer> channels;
    private List<String> groupChats;
    private List<Integer> genders;
    private String startTime;
    private String endTime;

    public Integer getStartIndex() {
        return (pageNum - 1) * pageSize;
    }
}
