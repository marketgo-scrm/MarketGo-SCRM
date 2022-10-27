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
public class WeComAddCorpTagRequest {
    /**
     * 企微 wecom
     * 通过数据仓库 cdp
     * 离线   offline
     */
    @ApiModelProperty(value = "标签组的id，创建标签的时候必传")
    private String groupId;
    @ApiModelProperty(value = "标签组的名称， 创建标签组的时候必传")
    private String groupName;
    @ApiModelProperty(value = "标签组的名称， 创建标签的时候必传")
    private List<String> tagNameList;
}
