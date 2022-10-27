package com.easy.marketgo.web.model.request.tags;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 4:17 PM
 * Describe:
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "添加标签的request")
public class WeComEditCorpTagRequest {

    private List<TagMessage> groups;
    private List<TagMessage> tags;

    @Data
    public static class TagMessage {
        @ApiModelProperty(value = "标签组或者标签的id")
        private String id;
        @ApiModelProperty(value = "标签组或者标签的名称")
        private String name;
    }
}
