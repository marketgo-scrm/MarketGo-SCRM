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
public class AddCorpTagsRequest {
    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("group_name")
    private String groupName;

    private Integer order;
    private List<TagMessage> tag;

    @JsonProperty("agentid")
    private Integer agentId;
    @Data
    public static class TagMessage {
        private String name;
        private Integer order;
    }
}
