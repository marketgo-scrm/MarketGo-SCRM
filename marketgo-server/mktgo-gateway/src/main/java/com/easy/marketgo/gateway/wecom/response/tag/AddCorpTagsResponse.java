package com.easy.marketgo.gateway.wecom.response.tag;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/22/22 2:47 PM
 * Describe:
 */
@Data
public class AddCorpTagsResponse extends WeComBaseResponse {

    @JsonProperty("tag_group")
    private TagGroup tagGroup;

    @Data
    public static class TagGroup {
        @JsonProperty("group_id")
        private String groupId;
        @JsonProperty("group_name")
        private String groupName;
        @JsonProperty("create_time")
        private Long createTime;
        private Integer order;

        private List<TagMessage> tag;
    }

    @Data
    public static class TagMessage {
        private String id;
        private String name;
        @JsonProperty("create_time")
        private Long createTime;
        private Integer order;
    }
}
