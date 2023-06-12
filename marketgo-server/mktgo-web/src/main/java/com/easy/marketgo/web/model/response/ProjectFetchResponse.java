package com.easy.marketgo.web.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:16:05
 * @description : ProjectFetchResponse.java
 */
@Data
@Builder
@ApiModel(description = "项目列表response")
public class ProjectFetchResponse {
    @ApiModelProperty(value = "租户uuid")
    private String tenantUuid;
    @ApiModelProperty(value = "是否能创建项目")
    private Boolean canCreate;
    @ApiModelProperty(value = "项目列表")
    private List<ProjectInfo> projects;

    @Data
    @Builder
    @ApiModel
    public static class ProjectInfo {
        @ApiModelProperty(value = "项目uuid")
        private String projectUuid;
        @ApiModelProperty(value = "项目名称")
        private String projectName;
        @ApiModelProperty(value = "项目状态")
        private String status;
        @ApiModelProperty(value = "项目描述")
        private String desc;
        @ApiModelProperty(value = "项目类型")
        private String type;
        @ApiModelProperty(value = "项目的创建时间")
        private String createTime;
    }
}
