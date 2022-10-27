package com.easy.marketgo.web.model.response.user;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 20:05:09
 * @description : OrganizationalStructureResponse.java
 */
@Data
@Builder
@ApiModel
public class OrganizationalStructureResponse {
    @ApiModelProperty(name = "structures", notes = "组织架构list", example = "")
    private List<StructureInfo> structures;

    @Data
    @Builder
    @ApiModel
    public static class StructureInfo {
        @ApiModelProperty(name = "parentDepartmentId", notes = "父部门id", example = "")
        private String parentDepartmentId;
        @ApiModelProperty(name = "id", notes = "部门id", example = "")
        private String id;
        @ApiModelProperty(name = "name", notes = "部门名称", example = "")
        private String name;
        @ApiModelProperty(name = "children", notes = "子部门列表", example = "")
        private List<StructureInfo> children = Lists.newArrayList();

    }

}
