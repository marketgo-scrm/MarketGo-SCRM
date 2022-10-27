package com.easy.marketgo.api.model.response.tag;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/22/22 2:47 PM
 * Describe:
 */
@Data
public class WeComAddCorpTagsClientResponse implements Serializable {

    private TagGroup tagGroup;

    @Data
    public static class TagGroup implements Serializable {
        private String groupId;
        private String groupName;
        private Long createTime;
        private Integer order;

        private List<TagMessage> tag;
    }

    @Data
    public static class TagMessage implements Serializable {
        private String id;
        private String name;
        private Long createTime;
        private Integer order;
    }
}
