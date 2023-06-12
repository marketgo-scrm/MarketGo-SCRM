package com.easy.marketgo.web.service.project;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.request.ProjectCreateRequest;
import com.easy.marketgo.web.model.response.ProjectFetchResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:10:30
 * @description : IProjectService.java
 */
public interface IProjectService {

    ProjectFetchResponse fetchProjects();

    BaseResponse createProject(ProjectCreateRequest projectCreateRequest);

    BaseResponse checkName(Integer projectId, String name);
}
