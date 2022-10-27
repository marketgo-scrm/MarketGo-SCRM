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
@ApiModel(description = "删除标签的request")
public class WeComDeleteCorpTagRequest {
    @ApiModelProperty(value = "删除标签组的Id")
    private List<String> groupId;
    @ApiModelProperty(value = "删除标签的Id")
    private List<String> tagId;
}
