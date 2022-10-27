package com.easy.marketgo.gateway.wecom.request.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:54 PM
 * Describe:
 */
@Data
public class EditCorpTagsRequest {
    private String id;

    private String name;

    private Integer order;

    @JsonProperty("agentid")
    private Integer agentId;
}
