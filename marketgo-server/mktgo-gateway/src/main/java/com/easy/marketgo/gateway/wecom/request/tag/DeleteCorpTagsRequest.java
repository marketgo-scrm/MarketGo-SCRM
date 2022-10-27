package com.easy.marketgo.gateway.wecom.request.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:54 PM
 * Describe:
 */
@Data
public class DeleteCorpTagsRequest {
    @JsonProperty("group_id")
    private List<String> groupId;

    @JsonProperty("tag_id")
    private List<String> tagId;

    @JsonProperty("agentid")
    private Integer agentId;
}
