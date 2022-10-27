package com.easy.marketgo.web.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 7:11 PM
 * Describe:
 */

@Data
@ApiModel(description = "企微客户标签列表response")
public class WeComCoreTagsResponse {
    @ApiModelProperty(value = "企业标签组列表")
    private List<WeComCorpTagGroup> corpTagGroups;

    @Data
    public static class WeComCorpTagGroup {
        @ApiModelProperty(value = "标签组id")
        private String groupId;
        @ApiModelProperty(value = "标签组名称")
        private String groupName;
        @ApiModelProperty(value = "标签组创建时间")
        private String createTime;
        @ApiModelProperty(value = "标签组排序的次序值，order值大的排序靠前。有效的值范围是[0, 2^32)")
        private Integer order;
        @ApiModelProperty(value = "标签组是否已经被删除，只在指定tag_id进行查询时返回")
        private Boolean deleted;
        @ApiModelProperty(value = "标签组内的标签列表")
        private List<WeComCorpTag> tags;
    }

    @Data
    public static class WeComCorpTag {
        @ApiModelProperty(value = "企业标签id")
        private String id;
        @ApiModelProperty(value = "企业标签名称")
        private String name;
        @ApiModelProperty(value = "企业标签创建时间")
        private String createTime;
        @ApiModelProperty(value = "标签排序的次序值，order值大的排序靠前。有效的值范围是[0, 2^32)")
        private Integer order;
        @ApiModelProperty(value = "标签是否已经被删除，只在指定tag_id/group_id进行查询时返回")
        private Boolean deleted;
    }
}
