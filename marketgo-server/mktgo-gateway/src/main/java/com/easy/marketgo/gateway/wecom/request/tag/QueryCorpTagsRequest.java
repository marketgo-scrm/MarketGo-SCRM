package com.easy.marketgo.gateway.wecom.request.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/22/22 3:04 PM
 * Describe:
 */
@Data
public class QueryCorpTagsRequest {
    @JsonProperty("tag_id")
    private List<String> tagIds;
    @JsonProperty("group_id")
    private List<String> groupIds;
}
