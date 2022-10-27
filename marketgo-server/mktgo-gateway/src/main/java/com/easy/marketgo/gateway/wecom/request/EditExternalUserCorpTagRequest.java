package com.easy.marketgo.gateway.wecom.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 5:41 PM
 * Describe:
 */
@Data
public class EditExternalUserCorpTagRequest {
    @JsonProperty("userid")
    private String userId;
    @JsonProperty("external_userid")
    private String externalUserId;
    @JsonProperty("add_tag")
    private List<String> addTag;

    @JsonProperty("remove_tag")
    private List<String> removeTag;
}
