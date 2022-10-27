package com.easy.marketgo.web.model.response.masstask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 11:41 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微群发创建人列表的response")
public class WeComMassTaskCreatorsResponse {
    @ApiModelProperty(value = "创建人列表详情")
    private List<CreatorMessage> creators;

    @Data
    public static class CreatorMessage {
        @ApiModelProperty(value = "创建人id")
        private String creatorId = null;
        @ApiModelProperty(value = "创建人名称")
        private String creatorName = null;
    }
}
