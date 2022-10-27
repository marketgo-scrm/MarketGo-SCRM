package com.easy.marketgo.api.model.request.tag;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:54 PM
 * Describe:
 */
@Data
public class WeComAddCorpTagsClientRequest extends BaseRpcRequest {
    private String groupId;

    private String groupName;

    private Integer order;
    private List<TagMessage> tag;

    @Data
    public static class TagMessage implements Serializable {
        private String name;
        private Integer order;
    }
}
