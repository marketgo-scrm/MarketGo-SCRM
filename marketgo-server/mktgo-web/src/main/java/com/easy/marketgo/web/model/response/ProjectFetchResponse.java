package com.easy.marketgo.web.model.response;

import io.swagger.annotations.ApiModel;
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
@ApiModel
public class ProjectFetchResponse {
    private String tenantUuid;

    private List<ProjectInfo> projects;

    @Data
    @Builder
    @ApiModel
    public  static  class ProjectInfo{
        private String projectUuid;
        private String projectName;
        private String status;
        private String desc;
        private String type;
        private String createTime;
    }
}
